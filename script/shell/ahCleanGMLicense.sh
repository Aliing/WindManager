#!/bin/bash
if [ -f /opt/amigopod/www/_site/AerohiveLicense.dat ]
then
  rm -rf /opt/amigopod/www/_site/AerohiveLicense.dat
fi

if [ -f /opt/amigopod/www/_site/AerohiveLicense2.dat ]
then
  rm -rf /opt/amigopod/www/_site/AerohiveLicense2.dat
fi