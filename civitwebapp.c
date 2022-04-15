#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>
#include <ctype.h>
#include <dirent.h>

#include "civetweb.h"

#define DOCUMENT_ROOT "."

#define PORT "8888"

#define TRUE 1

const char directory[25] = ".";
const char prefix[10] = "Collector";

static char * sev_values[8] = {
  "EMERGENCY",
  "ALERT",
  "CRITICAL",
  "ERROR",
  "WARNING",
  "Notice",
  "Informational",
  "Debug" };

static char * colors[3] = {
  "#FF0000",
  "#FFA500",
  "#FFFFFF" };

//! This function loops through the contents of a directory and 
//! finds the most recent log file matching a prefix.
int setCurrentLogFile(char * buffer, size_t buffer_size)
{
  struct dirent *entry;

  char temp[50];    // Temporary buffer
  char current[50]; // Hold the current file
  char time[10];    // Buffer to hold time

  char * pos;

  int index;
  int current_time;
  int temp_time;

  int foundOne;

  // Clear the buffers
  memset(buffer, '\0', buffer_size);
  memset(&temp, '\0', sizeof(temp));
  memset(&current, '\0', sizeof(current));
  memset(&time, '\0', sizeof(time));

  // Get directory contents
  DIR *dr = opendir(directory);

  if (dr == NULL)
  {
    return -1;
  }

  // Set the found flag to false and current_time to 0
  foundOne = 0;
  current_time = 0;

  // Loop through the directory to find files matching the prefix
  while ((entry = readdir(dr)) != NULL)
  {
    pos = NULL;
    index = 0;

    pos = strstr(entry->d_name, prefix);

    if (pos != NULL) // Found one that matches prefix
    {
      foundOne = 1;  // Set flag
      memset(&temp, '\0', sizeof(temp));
      strncpy(temp, entry->d_name, (size_t)strlen(entry->d_name));

      // Loop to find where the timestamp in seconds starts
      for (int i = 0; i < strlen(temp); i++)
      {
        if (temp[i] == '-')
        {
          index = i + 1;
          break;
        }
      }

      // Check for error
      if (index < 1)
      {
        continue;
      }

      // Get the timestamp
      int z = 0;
      while (temp[index] != '.')
      {
        time[z] = temp[index];
        z = z + 1;
        index = index + 1;
      }

      // Null terminate the time buffer; May not be necessary
      //time[z] = '\0';

      // Convert time
      temp_time = atoi(time);

      // Check to see if this is the more recent record
      if (current_time == 0)
      {
        current_time = temp_time;
        strncpy(current, temp, sizeof(temp));
        continue;
      }

      if (current_time < temp_time)
      {
        current_time = temp_time;
        strncpy(current, temp, sizeof(temp));
      }

      // Do Nothing
    } // End if
  } // End while

  // Check found flag
  if (foundOne != 1)
  {
    return -1;
  }  

  memcpy(buffer, current, buffer_size);
  fprintf(stdout, "Setting %s to current.\n", current);

  closedir(dr);
  return 0;
}

//! Function to parse a message in Syslog Format
static char * messageParse(
  char * message,
  char * buffer,
  int length,
  size_t size
)
{
  char prival[10];
  char time[30];
  char name[20];
  char prival_hold[4];
  char msg[100];

  char delim = ' ';
  int severity = -1;
  int start_point = -1;
  int count = 0;

  // Clear the buffer
  memset(buffer, '\0', size);

  // Get the PRIVAL
  for (start_point = 0; start_point < length; start_point++)
  {
    if (message[start_point] == delim)
      break;
    else
    {
      prival[count] = message[start_point];
      count = count + 1;
    }
  }

  // Reset destination array counter
  count = 0;

  // Get the date/time
  for (start_point = start_point + 1; start_point < length; start_point++)
  {
    if (message[start_point] == delim)
      break;
    else
    {
      time[count] = message[start_point];
      count = count + 1;
    }
  }
  time[count] = '\0';

  // Move the start point to the beginning of the name 
  for (start_point = start_point + 1; start_point < length; start_point++)
  {
    if (message[start_point] == delim)
      break;
  }

  // Reset destination array counter
  count = 0;

  // Get the name of the application/device
  for (start_point = start_point + 1; start_point < length; start_point++)
  {
    if (message[start_point] == delim)
      break;
    else
    {
      name[count] = message[start_point];
      count = count + 1;
    }
  }
  name[count] = '\0';
  
  // Now we need to find where the message itself begins
  for (start_point = start_point + 1; start_point < length; start_point++)
  {
    if (message[start_point] == ']')
    {
      break; // We have our starting point
    }
  }

  if (start_point < length - 1)
  {
    start_point = start_point + 1;
    count = 0;

    // Get the message contents
    while (start_point < length)
    {
      msg[count] = message[start_point];
      count = count + 1;
      start_point = start_point + 1;
    }
    msg[count] = '\0';
    int extra_count = 0;

    // Now let's deal with the PRIVAL
    for (count = 0; count < strlen(prival); count++)
    {
      if (prival[count] == '>')
        break;
      if (isdigit(prival[count]))
      {
        prival_hold[extra_count] = prival[count];
        extra_count = extra_count + 1;
      }
    }

    // Calculate severity
    severity = atoi(prival_hold);
    severity = severity % 8;

    char * sev = sev_values[severity];
    char * color = NULL;

    // Get the color of the message, given the severity
    if (severity < 4)
      color = colors[0];
    else if (severity == 4)
      color = colors[1];
    else
      color = colors[2];

    // Now let's piece this thing together
    snprintf(
      buffer,
      size,
      "<p style=\"color:%s;\">%s - %s: %s %s</p>",
      color,
      sev,
      name,
      time,
      msg
    );
    return buffer;
  }

  // If we get here, there's nothing we can do
  (void) buffer;
  return NULL;

}

static int handler(struct mg_connection *conn, void *ignored)
{
  char file[50];
  size_t size = 1024;

  int returned;

  char line[size];
  char * buffer_m;
  buffer_m = (char*)malloc(sizeof(char) * size);

  mg_printf(conn,
    "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nConnection: "
    "close\r\n\r\n"
  );

  mg_printf(conn, "<!DOCTYPE html>\n");
  mg_printf(conn, "<html>\n");
  mg_printf(conn, "<head>\n");
  mg_printf(conn, "<title>Arke Collector Queue</title>\n");
  mg_printf(conn, "<script>\n");
  mg_printf(conn, "function autoRefresh() {\n");
  mg_printf(conn, "window.location = window.location.href;\n");
  mg_printf(conn, "}\n");
  mg_printf(conn, "window.onload = function() {\n");
  mg_printf(conn, "var scrollingElement = (document.scrollingElement || document.body);\n");
  mg_printf(conn, "scrollingElement.scrollTop = scrollingElement.scrollHeight;\n");
  mg_printf(conn, "}\n");
  mg_printf(conn, "setInterval('autoRefresh()', 5000);\n");
  mg_printf(conn, "</script>\n");
  mg_printf(conn, "</head>\n");
  mg_printf(conn, "<body style=\"color:#00FF00; background-color:#000000\">\n");

  // Get the input file
  returned = setCurrentLogFile(file, sizeof(file));

  if (returned != 0)
  {
    mg_printf(conn, "<h1>Arke Collector Not Running.</h1>\n");
    mg_printf(conn, "</body>\n");
    mg_printf(conn, "</html>\n");

    return 200; /* HTTP state 200 = OK */
  }

  FILE *fp = fopen(file, "r");

  if (fp == NULL)
  {
    mg_printf(conn, "<h1>Arke Collector Not Running.</h1>\n");
    mg_printf(conn, "</body>\n");
    mg_printf(conn, "</html>\n");

    return 200; /* HTTP state 200 = OK */
  }

  int i = 1;
  /* We don't need the first two lines */
  while (i < 3)
  {
    if (fgets(line, sizeof(line), fp) != NULL)
    {
      i = i + 1;
      continue;
    }
    else
      break;
  }

  (void) i;

  mg_printf(conn, "<h1></h1>\n");

  while (fgets(line, sizeof(line), fp) != NULL)
  {
    buffer_m = messageParse(line, buffer_m, (int)strlen(line), size);
    mg_printf(conn, "%s\n", buffer_m);

    //mg_printf(conn, "<p>%s</p>\n", line);
    //fprintf(stdout, "%s\n", messageParse(line, buffer_m, (int)strlen(line), size));
  }
  mg_printf(conn, "</body>\n");
  mg_printf(conn, "</html>\n");

  fclose(fp);

  return 200; /* HTTP state 200 = OK */
}

int main(int argc, char *argv[])
{

  /* Server context handle */
  struct mg_context *ctx;

  /* Initialize the library */
  mg_init_library(0);

  /* Start the server */
  ctx = mg_start(NULL, 0, NULL);

  /* Add some handler */
  mg_set_request_handler(ctx, "/Arke", handler, "Arke Monitor");

  /* ... Run the application ... */

  system("/usr/bin/firefox http://localhost:8080/Arke --window-size=1000,800");

  while (TRUE)
  {
    sleep(1);
  }

  /* Stop the server */
  mg_stop(ctx);

  /* Un-initialize the library */
  mg_exit_library();
  return 0;
}

