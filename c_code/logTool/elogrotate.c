#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <time.h>
#include <stdlib.h>
#include <dirent.h>
#include <sys/stat.h>


#define MAX_LOG_NUM 30


char g_rotate_file[1024];
char g_rotate_file_path[1024];
char g_rotate_file_name[256];
int  g_rotate_num;
int  g_rotate_size;

void
clear_buffer (void *buffer, int buffer_size)
{
  memset (buffer, '\0', buffer_size);
}


void printHelp()
{
    printf("elogrotate -f File -n rotatetimes -s size -h help \n");
	printf("the parameters -f, -n, -s must use together \n");
	printf("-f: which and where log file need rotate \n");
	printf("-n: the rotate times for the rotate log, the Max is 30 times\n");
	printf("-s: which size need to be rotated. The unit is M\n");
}

int splitPath(char* strFile)
{
    int error_code = 0;

    char* p = strrchr(strFile, '/');

    if( NULL == p)
    {
         error_code = 1;
		 goto end;
	}
	clear_buffer(g_rotate_file_path, sizeof(g_rotate_file_path));
    clear_buffer(g_rotate_file_name, sizeof(g_rotate_file_name));
	
	strncpy(g_rotate_file_path, strFile, p-strFile);
	strncpy(g_rotate_file_name, ++p, sizeof(g_rotate_file_name));

end:
	return error_code;
}

int getOpt(int argc, char** argv)
{
    int error_code = 0;
    int ch;

    opterr = 0;
	g_rotate_num = 0;
	g_rotate_size = 0;
	clear_buffer(g_rotate_file, sizeof(g_rotate_file));

    while((ch = getopt(argc, argv, "f:n:s:h")) != -1)
    {
      switch(ch)
      {
      case 'f':
        strncpy(g_rotate_file, optarg, sizeof(g_rotate_file));
		break;
	  case 'n':
        g_rotate_num = atoi(optarg);
        if( 0 >= g_rotate_num || MAX_LOG_NUM < g_rotate_num)
        {
            printf("The rotate number must bigger than 0 and smaller than 30\n");
			error_code = 1;
			goto end;
		}		
		break;
	  case 's':
        g_rotate_size = atoi (optarg);

        if( 0 >= g_rotate_size)
        {
           printf("The rotate size must a Integer and bigger than 0 \n ");
		   error_code = 1;
		   goto end;
		}
		
		break;
	  case 'h':       
		error_code = 1;
		goto end;
		break;
	  default:
        
		break;
	  }
	}

	if((strlen(g_rotate_file) <= 0) || (g_rotate_num <= 0) || (g_rotate_size <= 0))
	{      
	   error_code = 1;
	}
end:

   if( 1 == error_code)
   {
        printHelp();
   }
	
	return error_code;
}


int backLog()
{
    int error_code = 0;

    char strNewName[1024];

    clear_buffer(strNewName, sizeof(strNewName));

    time_t renametime;
    struct tm *tminfo;

    time(&renametime);

    tminfo = localtime(&renametime);

    snprintf(strNewName, sizeof(strNewName), "%s.%04d%02d%02d%02d%02d%02d", g_rotate_file, (tminfo->tm_year+1900),
   	                    (tminfo->tm_mon+1), tminfo->tm_mday, tminfo->tm_hour, tminfo->tm_min, tminfo->tm_sec);

    //back the file
    char cmd[1024];
    clear_buffer(cmd, sizeof(cmd));

    snprintf(cmd, sizeof(cmd), "%s %s %s", "/bin/cp -rf", g_rotate_file, strNewName);
   
    if( 0 != (error_code = system(cmd)))
    {
         printf("%s has some error! \n", cmd);
		 goto end;
	}

    clear_buffer(cmd, sizeof(cmd));

	//clean the use log
	snprintf(cmd, sizeof(cmd), "%s %s", "echo "" >", g_rotate_file);
	if( 0 != (error_code = system(cmd)))
    {
         printf("%s has some error! \n", cmd);
		 goto end;
	}

end:

    return error_code;
   
}

int rotateLog()
{
    int error_code = 0;
	int log_num = 0;

    DIR *pDir = NULL;
	char *fileNamelist[MAX_LOG_NUM+1];
	struct dirent *pDirent = NULL;
    memset(fileNamelist,0,sizeof(fileNamelist));
	
    char filter[1024];
	clear_buffer(filter, sizeof(filter));
	snprintf(filter, sizeof(filter), "%s.", g_rotate_file_name);

	if( NULL == (pDir = opendir(g_rotate_file_path)))
	{
       perror("open log dir ");
	   error_code = 1;
	   goto end;
	}

	while(NULL != (pDirent = (readdir(pDir))))
	{
      if(NULL != strstr(pDirent->d_name, filter) && log_num <= MAX_LOG_NUM+1)
      {
        fileNamelist[log_num] = (char *)malloc(sizeof(pDirent->d_name));
		
        strcpy(fileNamelist[log_num],pDirent->d_name);
 
		log_num++;
	  }
	}

	closedir(pDir);

	if(log_num < g_rotate_num+1)
	{
	    goto end;
	}

	struct stat filestat;

	int i = 0;

    int fileIndex = 0;

	time_t tmpTm = 0;

	char tmpFile[1024];

	clear_buffer(tmpFile,sizeof(tmpFile));

    
	snprintf(tmpFile, sizeof(tmpFile),"%s/%s",g_rotate_file_path,fileNamelist[i]);
	
    stat(tmpFile, &filestat);

	tmpTm = filestat.st_mtime;
	fileIndex = i;

	i=1;

	while(NULL != fileNamelist[i])
	{
      //get the file 
      snprintf(tmpFile, sizeof(tmpFile),"%s/%s",g_rotate_file_path,fileNamelist[i]);

	  stat(tmpFile, &filestat);

	  if(tmpTm > filestat.st_mtime)
	  {
          tmpTm = filestat.st_mtime;
		  fileIndex = i;
	  }

	   i++;
	}

     snprintf(tmpFile, sizeof(tmpFile),"%s/%s",g_rotate_file_path,fileNamelist[fileIndex]);
    //delete the old  file
    
	if(0 != remove(tmpFile))
	{
      perror("remove the old log file");
	}

end:

	i = 0;

	while(NULL != fileNamelist[i])
	{
      free(fileNamelist[i]);

	  i++;
	}

	return error_code;
	
}

int main(int argc, char** argv)
{

    struct stat filestat;

    int error_code = 0;

	if((error_code = getOpt(argc, argv)) != 0 )
    {
        goto end;
	}

	if((error_code = splitPath(g_rotate_file)) != 0)
	{
        goto end;
	}

    //get the file info
    stat(g_rotate_file, &filestat);

    //judge file size

	if(filestat.st_size >= g_rotate_size*1024*1024)
	{
        //backlog
	    if(0 != (error_code = backLog()))
	    {
            goto end;
	    }	   
	}

	 //rotate log 

	if(0 != (error_code = rotateLog()))
	{
        goto end;
	}

end:
	return error_code;
}
