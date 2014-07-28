# !/bin/bash
host="localhost"
port="443"

while getopts ":h:p:" opt; do
    case $opt in
        h)
        host=$OPTARG
        ;;
        p)
        port=$OPTARG
        ;;
        :)
        echo "Option -$OPTARG requires an argument." >&2
        exit 1
        ;;
        ?)
        echo "Usage: $0 [-h HOST] [-p PORT]" >&2
        exit 1
        ;;
    esac
done

curl -s -f -k --connect-time 30 -m 30 https://$host:$port/hm/healthcheck?t=c
exit $?