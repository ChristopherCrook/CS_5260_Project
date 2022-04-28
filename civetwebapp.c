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
const char log_file[14] = "alien_log.txt";

static int handler(struct mg_connection *conn, void *ignored)
{
  char file[14] = "alien_log.txt";
  size_t size = 1024;
  
  char line[size];

  mg_printf(conn,
    "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nConnection: "
    "close\r\n\r\n"
  );

  mg_printf(conn, "<!DOCTYPE html>\n");
  mg_printf(conn, "<html>\n");
  mg_printf(conn, "<head>\n");
  mg_printf(conn, "<title>Alien Invasion</title>\n");
  mg_printf(conn, "<script>\n");
  mg_printf(conn, "function autoRefresh() {\n");
  mg_printf(conn, "window.location = window.location.href;\n");
  mg_printf(conn, "}\n");
  mg_printf(conn, "window.onload = function() {\n");
  mg_printf(conn, "var scrollingElement = (document.scrollingElement || document.body);\n");
  mg_printf(conn, "scrollingElement.scrollTop = scrollingElement.scrollHeight;\n");
  mg_printf(conn, "}\n");
  mg_printf(conn, "setInterval('autoRefresh()', 3000);\n");
  mg_printf(conn, "</script>\n");
  mg_printf(conn, "</head>\n");
  mg_printf(conn, "<body style=\"color:#00FF00; background-color:#000000\">\n");

  FILE *fp = fopen(file, "r");

  if (fp == NULL)
  {
    mg_printf(conn, "<h1>Invasion has not started.</h1>\n");
    mg_printf(conn, "</body>\n");
    mg_printf(conn, "</html>\n");

    return 200; /* HTTP state 200 = OK */
  }

  mg_printf(conn, "<h1></h1>\n");
  
  char *pt;

  while (fgets(line, sizeof(line), fp) != NULL)
  {
    pt = strtok(line, ",");
    while (pt != NULL)
    {
      mg_printf(conn, "<h1>%s</h1>\n", pt);
      pt = strtok(NULL, ",");
    }
    //mg_printf(conn, "<p>%s</p>\n", line);
    
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
  mg_set_request_handler(ctx, "/AlienInvasion", handler, "Alien Invasion");

  /* ... Run the application ... */

  system("/usr/bin/firefox http://localhost:8080/AlienInvasion --window-size=1000,800");

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

