package com.ah.be.admin;


import  java.net.*;
import  java.util.Enumeration;


/** Local Addresses

  @since    March 27, 2012
  @version  1.0
  @author   <a hRef=MailTo:yZhong@AeroHive.com>&#38047;&#38451;(Yang ZHONG)</a>
*/
public  class LocalAddresses
{
/** Local Addresses Iteration

  @since    March 27, 2012
  @version  1.0
  @author   <a hRef=MailTo:yZhong@AeroHive.com>&#38047;&#38451;(Yang ZHONG)</a>
*/
  public  interface Iteration
  {
    boolean stop  (String address,String network);
  }
  public  static  String  iterate (Iteration iteration) throws  SocketException
  {
    for(final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces() ; interfaces.hasMoreElements() ;)
      //  SocketException may be thrown
    {
      final NetworkInterface  network = interfaces.nextElement();
      final String  name = network.getName();
      if( ! "lo".equals( name) )  //  Windows 7 SP1 x64 & cEntOS 5 x64
        for(final Enumeration<InetAddress> addresses = network.getInetAddresses() ; addresses.hasMoreElements() ;)
        {
          final InetAddress address = addresses.nextElement();
          if( ! (address instanceof Inet4Address) )
            continue;
          final String  ip = address.getHostAddress();
          if( iteration.stop( ip, name) )
            return ip;
        }
    }
    return null;
  }

  private static  final Iteration ITERATION = new Iteration()
  {
    public  boolean stop  (String address,String network)
    {
      return true;
    }
  };
  public  static  String  get() throws  SocketException
  {
    return iterate( ITERATION);
  }
}