#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <openssl/pem.h>
#include <openssl/x509v3.h>


static int get_subjectAltName(X509 *s_ctx);
static int get_subject(X509 *s_ctx);

int main(int argc, char *argv[])
{
    int error_code = 0;

    if(2 > argc)
    {
        printf("args is error\n");
        error_code = 2;
        goto end;
    }

    char *certfile = argv[1];
    FILE *fpem = NULL;
    X509 *cert = NULL;

    if( !( fpem = fopen( certfile, "r" ))) 
    {
        printf("Couldn't open the PEM file: %s\n", certfile);
        error_code = 3;
        goto end;
    }

    if( !( cert = PEM_read_X509( fpem, NULL, NULL, NULL ))) 
    {
        printf( "Failed to read the PEM file: %s\n",certfile );
        error_code = 4;
        goto end;
    }
    if(fpem)
    {
        fclose(fpem);
    }

	error_code = get_subject(cert);

    error_code = get_subjectAltName(cert);
   
end:

    if(cert)
    {
        X509_free(cert);
    }

  return error_code;
}


static int get_subject(X509 *s_ctx)
{
    int error_code = 0;
	char *str = NULL;

	str = X509_NAME_oneline (X509_get_subject_name (s_ctx), NULL, 0);

    if(NULL != str)
    {
        printf("Subject:%s\n", str);
	}

	OPENSSL_free(str);

    return error_code;
}


static int get_subjectAltName(X509 *s_ctx)
{
    int error_code = 0;
    GENERAL_NAMES *gens = NULL;
    GENERAL_NAME *gen = NULL;
  
    char *altname;
    unsigned char p[5], *ip;

    gens = X509_get_ext_d2i(s_ctx, NID_subject_alt_name, NULL, NULL);

    if(NULL == gens)
    {
        error_code = 0;
        goto end;
    }

    int gen_num = sk_GENERAL_NAME_num(gens);

    int i=0;
    for(i=0; i <= gen_num; ++i)
    {
        gen = sk_GENERAL_NAME_value(gens, i);
        if(NULL == gen)
        {
            continue;
        }

        if (gen->type == GEN_DNS 
        ||gen->type == GEN_EMAIL 
        ||gen->type == GEN_URI )
        {
            // make sure if the data is terminated by '\0'. 
            if (gen->d.ia5->data[gen->d.ia5->length] != '\0') 
            {
                continue;
            }

            altname = (char *)malloc(gen->d.ia5->length + 1);

            strncpy(altname, (char *) gen->d.ia5->data, (gen->d.ia5->length + 1));

            switch (gen->type) {
            case GEN_EMAIL:
                printf("email:%s\n", altname);
                break;
            case GEN_DNS:
                printf("DNS:%s\n", altname);
                break;
            case GEN_URI:
            default:
                printf("URI:%s\n", altname);
                break;    
            }
        }
        else if (gen->type == GEN_IPADD)
        {
            if (gen->d.ip->length != 4) {
                continue;
              }
            ip = p;
            ip = gen->d.ip->data;
            printf("IP:%u.%u.%u.%u\n", ip[0], ip[1], ip[2], ip[3]);
        }


        if(altname)
        {
            free(altname);
            altname = NULL;
        }     
    }

end:
    if(gens)
    {
        // GENERAL_NAMES_free(gens);
        sk_GENERAL_NAME_pop_free(gens, GENERAL_NAME_free);
    }

    return error_code;
}

