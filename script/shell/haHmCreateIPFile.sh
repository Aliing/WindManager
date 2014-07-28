#!/bin/bash
if [ ! -f $3 ]
then
	touch $3
fi

echo "$1:$2" >> $3