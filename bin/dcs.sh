#!/bin/bash
#
# lighttpd     Startup script for the lighttpd server
#
# chkconfig: - 85 15
# description: Lighttpd web server
#
# processname: lighttpd

# Source function library.
. /etc/rc.d/init.d/functions

conf="/etc/lighttpd/lighttpd.conf"
prog=lighttpd
lighttpd="/usr/sbin/lighttpd"
pidfile="/var/run/lighttpd.pid"
user="lighttpd"

# get custome config
if [ -f /etc/sysconfig/lighttpd ]; then
        . /etc/sysconfig/lighttpd
fi


start(){
}

stop(){
}

reload(){
}

status(){
}

case "$1" in
        start)
                start
                ;;
        stop)
                stop
                ;;
        restart)
                stop
                start
                ;;
        reload)
                reload
		;;
        status)
                status
                ;;
        *)
                echo $"Usage: $0 {start|stop|restart|reload|status}"
esac

# start lighttpd web server
start(){
	echo -n "Starting "
        $lighttpd -f $conf
	[ $? -eq 0 ] && echo " [ OK ] " || echo " [ FAILED ] "
}

# stop lighttpd web server
stop(){
        echo -n $"Stopping $prog "
        [ -f "$pidfile" ] && read line < "$pidfile"
        if [ -d "/proc/$line" -a -f $conf ]
        then
  	      pkill -KILL -u $user $prog
              [ $? -eq 0 ] && echo " [ OK ] " || echo " [ FAILED ] "
              [ -f "$pidfile" ] && rm -f "$pidfile"
	fi
}

#https://bash.cyberciti.biz/guide/Service_command
#https://www.cyberciti.biz/tips/linux-write-sys-v-init-script-to-start-stop-service.html