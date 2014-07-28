#!/bin/bash

grep "rest.key.redirector@hmol=" /etc/secretkeys > /dev/null 2>&1
if [  $? -ne 0 ]; then
	echo "" >> /etc/secretkeys
	echo "rest.key.redirector@hmol=aerohive" >> /etc/secretkeys
fi

grep "rest.principal.redirector@hmol=" /etc/secretkeys > /dev/null 2>&1
if [  $? -ne 0 ]; then
	echo "" >> /etc/secretkeys
	echo "rest.principal.redirector@hmol=redirector_user" >> /etc/secretkeys
fi

grep "rest.key.portal@hmol=" /etc/secretkeys > /dev/null 2>&1
if [  $? -ne 0 ]; then
	echo "" >> /etc/secretkeys
	echo "rest.key.portal@hmol=aerohive" >> /etc/secretkeys
fi

grep "rest.principal.portal@hmol=" /etc/secretkeys > /dev/null 2>&1
if [  $? -ne 0 ]; then
	echo "" >> /etc/secretkeys
	echo "rest.principal.portal@hmol=portal_user" >> /etc/secretkeys
fi