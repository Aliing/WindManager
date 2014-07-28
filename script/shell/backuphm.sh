#!/bin/bash
if [ -d /var/lib/pgsql/backups ]
then
  su postgres -c "pg_dump hm | gzip > /var/lib/pgsql/backups/hm.gz" > /dev/null 2>&1
fi