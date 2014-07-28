#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <openssl/ssl.h>

static int no_passphrase_callback(char *buf, int num, int w, void *arg);

int main(int argc, char *argv[])
{
  if(3 > argc)
  {
    printf("args is error\n");
    return 2;
  }

  char *certfile = argv[1];
  char *keyfile = argv[2];
  char *passwd ;

  if (4 == argc)
  {
    passwd = argv[3];
  }

  SSL_CTX* ctx;
 
  SSL_load_error_strings(); 
  SSLeay_add_ssl_algorithms(); 
  OpenSSL_add_ssl_algorithms();

  ctx = SSL_CTX_new(SSLv23_method());

  if (!ctx)
  {
    printf("ctx is error\n");
    return 2;
  }

  SSL_CTX_set_default_passwd_cb(ctx, no_passphrase_callback);
 
  if(passwd)
  {
     SSL_CTX_set_default_passwd_cb_userdata(ctx, passwd);
  }
  
   if (keyfile && !SSL_CTX_use_PrivateKey_file(ctx, keyfile, SSL_FILETYPE_PEM) )
  {
    printf("invalid PrivateKey file or key password\n");
    SSL_CTX_free(ctx);
    return 3;
  }

  if (certfile && !SSL_CTX_use_certificate_file(ctx, certfile, SSL_FILETYPE_PEM) )
  {
    printf("invalid certficate file\n");
    SSL_CTX_free(ctx);
    return 4;
  }

   if(keyfile && certfile && !SSL_CTX_check_private_key(ctx))
  {
    printf("certificate and PrivateKey is not matching\n");
    SSL_CTX_free(ctx);
    return 5;
  }


  SSL_CTX_free(ctx);

  printf("check is ok\n");
  return 0;
}

static int no_passphrase_callback(char *buf, int size, int w, void *password)
{
  if(password == NULL)
  {
    return 0;
  }

  strncpy(buf,(char *)password, size);
  buf[size -1]=0;
  return strlen(buf);
}
