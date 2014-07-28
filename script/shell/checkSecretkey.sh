#!/bin/bash

grep "redirector.rest.key.hmol@redirector=" /etc/secretkey > /dev/null 2>&1
if [  $? -ne 0 ]; then
	echo "" >> /etc/secretkey
	echo "redirector.rest.key.hmol@redirector=aerohive" >> /etc/secretkey
fi

grep "licserver.rest.key.hmol@licserver=" /etc/secretkey > /dev/null 2>&1
if [  $? -ne 0 ]; then
	echo "" >> /etc/secretkey
	echo "licserver.rest.key.hmol@licserver=aerohive" >> /etc/secretkey
fi

grep "portal.rest.key.hmol@portal=" /etc/secretkey > /dev/null 2>&1
if [  $? -ne 0 ]; then
	echo "" >> /etc/secretkey
	echo "portal.rest.key.hmol@portal=aerohive" >> /etc/secretkey
fi

grep "salesforce.rest.key.aerohive-JCPR80=" /etc/secretkey > /dev/null 2>&1
if [  $? -ne 0 ]; then
	echo "" >> /etc/secretkey
	echo "salesforce.rest.key.aerohive-JCPR80=32ae9766-ea60-4e3c-998e-c1706bbb8d2a" >> /etc/secretkey
fi

grep "guestAnalytics.rest.key.guest-analytics=" /etc/secretkey > /dev/null 2>&1
if [  $? -ne 0 ]; then
	echo "" >> /etc/secretkey
	echo "guestAnalytics.rest.key.guest-analytics=9T2RWY8h5TWQDB9M" >> /etc/secretkey
fi