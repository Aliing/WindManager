# !/bin/bash
sleep 1
hwclock --systohc --localtime
sleep 1
hwclock -w
sleep 1
hwclock --hctosys
sleep 1
