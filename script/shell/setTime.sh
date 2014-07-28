if [ $# == 1 ]
then
date $1

sleep 10

hwclock --systohc

sleep 1

hwclock -w 

sleep 1

else
	echo "need the date"
	exit 1
fi