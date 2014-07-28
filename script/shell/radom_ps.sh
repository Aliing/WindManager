#!/bin/bash

chars=({0..9} {a..z} {A..Z} + - _ = % ! )
i=1
len=32

while [ $i -le $len ]
do
  echo -n ${chars[$RANDOM % ${#chars[*]}]};
  ((i++))
done
echo 
