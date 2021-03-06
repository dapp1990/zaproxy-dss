/*
 * Created on May 25, 2004
 *
 * Paros and its related class files.
 * 
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2003-2004 Chinotec Technologies Company
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Clarified Artistic License
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Clarified Artistic License for more details.
 * 
 * You should have received a copy of the Clarified Artistic License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
// ZAP: 2011/05/09 Support for API
// ZAP: 2011/05/15 Support for exclusions
// ZAP: 2012/03/15 Removed unnecessary castings from methods notifyListenerRequestSend,
// notifyListenerResponseReceive and isProcessCache. Set the name of the proxy thread.
// Replaced the class HttpBody with the new class HttpRequestBody and replaced the method 
// call from readBody to readRequestBody of the class HttpInputStream. 
// ZAP: 2012/04/25 Added @Override annotation to the appropriate method.
// ZAP: 2012/05/11 Do not close connections in final clause of run() method,
// if boolean attribute keepSocketOpen is set to true.
// ZAP: 2012/08/07 Issue 342 Support the HttpSenderListener
// ZAP: 2012/11/04 Issue 408: Add support to encoding transformations, added an
// option to control whether the "Accept-Encoding" request-header field is 
// modified/removed or not.
// ZAP: 2012/12/27 Added support for PersistentConnectionListener.
// ZAP: 2013/01/04 Do beginSSL() on HTTP CONNECT only if port requires so.
// ZAP: 2013/03/03 Issue 547: Deprecate unused classes and methods
// ZAP: 2013/04/11 Issue 621: Handle requests to the proxy URL
// ZAP: 2013/04/14 Issue 622: Local proxy unable to correctly detect requests to itself
// ZAP: 2013/06/17 Issue 686: Log HttpException (as error) in the ProxyThread
// ZAP: 2013/12/13 Issue 939: ZAP should accept SSL connections on non-standard ports automatically
// ZAP: 2014/03/06 Issue 1063: Add option to decode all gzipped content
// ZAP: 2014/03/23 Tidy up, extracted a method that writes an HTTP response and moved the
// code responsible to decode a GZIP response to a method
// ZAP: 2014/03/23 Fixed an issue with ProxyThread that happened when the proxy was set to listen on
// any address in which case the requests to the proxy itself were not correctly detected.
// ZAP: 2014/03/23 Issue 122: ProxyThread logging timeout readings with incorrect message (URL)
// ZAP: 2014/03/23 Issue 585: Proxy - "502 Bad Gateway" errors responded as "504 Gateway Timeout"
// ZAP: 2014/03/23 Issue 969: Proxy - Do not include the response body when answering unsuccessful HEAD requests
// ZAP: 2014/03/23 Issue 1017: Proxy set to 0.0.0.0 causes incorrect PAC file to be generated
// ZAP: 2014/03/23 Issue 1022: Proxy - Allow to override a proxied message
// ZAP: 2014/04/17 Issue 1156: Proxy gzip decoder doesn't update content length in response headers
// ZAP: 2014/05/01 Issue 1156: Proxy gzip decoder removes newlines in decoded response
// ZAP: 2014/05/01 Issue 1168: Add support for deflate encoded responses
// ZAP: 2015/01/04 Issue 1334: ZAP does not handle API requests on reused connections
// ZAP: 2015/02/24 Issue 1540: Allow proxy scripts to fake responses
// ZAP: 2015/07/17 Show stack trace of the exceptions on proxy errors
// ZAP: 2016/03/18 Issue 2318: ZAP Error [java.net.SocketTimeoutException]: Read timed out when running on AWS EC2 instance
// ZAP: 2016/04/13 Notify of timeouts when reading a response
// ZAP: 2016/04/14 Delay the write of response to not attempt to write a response again when handling IOException
// ZAP: 2016/04/29 Adjust exception logging levels and log when timeouts happen
// ZAP: 2016/05/30 Issue 2494: ZAP Proxy is not showing the HTTP CONNECT Request in history tab
// ZAP: 2016/06/13 Remove all unsupported encodings (instead of just some)
// ZAP: 2016/09/22 JavaDoc tweaks

package org.parosproxy.paros.core.proxy;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.core.proxy.notification.ConnectMessageNotifier;
import org.parosproxy.paros.core.proxy.notification.ProxyListenerNotifier;
import org.parosproxy.paros.core.proxy.notification.ListenerRequestSendNotifier;
import org.parosproxy.paros.core.proxy.notification.ListenerResponseReceiveNotifier;
import org.parosproxy.paros.core.proxy.notification.OverrideListenersRequestSendNotifier;
import org.parosproxy.paros.core.proxy.notification.OverrideListenersResponseReceivedNotifier;
import org.parosproxy.paros.core.proxy.notification.PersistentConnectionListenerNotifier;
import org.parosproxy.paros.db.RecordHistory;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.ConnectionParam;
import org.parosproxy.paros.network.HttpHeader;
import org.parosproxy.paros.network.HttpInputStream;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpOutputStream;
import org.parosproxy.paros.network.HttpRequestHeader;
import org.parosproxy.paros.network.HttpSender;
import org.parosproxy.paros.network.HttpUtil;
import org.parosproxy.paros.security.MissingRootCertificateException;
import org.zaproxy.zap.ZapGetMethod;
import org.zaproxy.zap.extension.api.API;
import org.zaproxy.zap.network.HttpRequestBody;


class ProxyThread implements Runnable {

//	private static final int		BUFFEREDSTREAM_SIZE = 4096;
	private static final String		CONNECT_HTTP_200 = "HTTP/1.1 200 Connection established\r\nProxy-connection: Keep-alive\r\n\r\n";
//	private static ArrayList 		processForwardList = new ArrayList();
    
	private static Logger log = Logger.getLogger(ProxyThread.class);

    private static final String BAD_GATEWAY_RESPONSE_STATUS = "502 Bad Gateway";
    private static final String GATEWAY_TIMEOUT_RESPONSE_STATUS = "504 Gateway Timeout";
    
	// change httpSender to static to be shared among proxies to reuse keep-alive connections

	protected ProxyServer parentServer = null; 
	protected ProxyParam proxyParam = null; 
	protected ConnectionParam connectionParam = null;
	protected Thread thread = null; 
	protected Socket inSocket	= null;
	
	protected Socket outSocket = null; 
	protected ProxyThread originProcess = this;
	
	protected HttpInputStream httpIn = null; 
	protected HttpOutputStream httpOut = null;

	
	private HttpSender 		httpSender = null;
	private Object semaphore = this;
	private HashMap<String, ProxyListenerNotifier> notificationHttp = new HashMap<String, ProxyListenerNotifier>();
	
	// ZAP: New attribute to allow for skipping disconnect
	private boolean keepSocketOpen = false;
	
	private static Object semaphoreSingleton = new Object();
	private static int id = 1;
    
    private static Vector<Thread> proxyThreadList = new Vector<>();
    
	ProxyThread(ProxyServer server, Socket socket) {
		parentServer = server;
		proxyParam = parentServer.getProxyParam();
		connectionParam = parentServer.getConnectionParam();
		
		notificationHttp.put("ListenerRequestSend", new ListenerRequestSendNotifier());
		notificationHttp.put("ListenerResponseReceive", new ListenerResponseReceiveNotifier());
		notificationHttp.put("OverrideListenersRequestSend", new OverrideListenersRequestSendNotifier());
		notificationHttp.put("OverrideListenersResponseReceived", new OverrideListenersResponseReceivedNotifier());
		notificationHttp.put("ConnectMessage", new ConnectMessageNotifier());
		notificationHttp.put("PersistentConnectionListener", new PersistentConnectionListenerNotifier());
		
		inSocket = socket;
    	try {
			inSocket.setTcpNoDelay(true);
			// ZAP: Set timeout
    		inSocket.setSoTimeout(connectionParam.getTimeoutInSecs() * 1000);
		} catch (SocketException e) {
			// ZAP: Log exceptions
			log.warn(e.getMessage(), e);
		}

		thread = new Thread(this, "ZAP-ProxyThread-" + id++); // ZAP: Set the name of the thread.
		thread.setDaemon(true);
		thread.setPriority(Thread.NORM_PRIORITY-1);
	}

	public void start() {
		thread.start();
        
	}


	@Override
	public void run() {
        proxyThreadList.add(thread);
		boolean isSecure = this instanceof ProxyThreadSSL;
		HttpRequestHeader firstHeader = null;
		
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inSocket.getInputStream(), 2048);
			inSocket = new CustomStreamsSocket(inSocket, bufferedInputStream, inSocket.getOutputStream());

			httpIn = new HttpInputStream(inSocket);
			httpOut = new HttpOutputStream(inSocket.getOutputStream());
			
			firstHeader = httpIn.readRequestHeader(isSecure);
            
			if (firstHeader.getMethod().equalsIgnoreCase(HttpRequestHeader.CONNECT)) {
				HttpMessage connectMsg = new HttpMessage(firstHeader);
				connectMsg.setTimeSentMillis(System.currentTimeMillis());
				try {
					httpOut.write(CONNECT_HTTP_200);
					httpOut.flush();
					connectMsg.setResponseHeader(CONNECT_HTTP_200);
					connectMsg.setTimeElapsedMillis((int) (System.currentTimeMillis() - connectMsg.getTimeSentMillis()));
					
					notificationHttp.get("ConnectMessage").notify(this.parentServer, connectMsg);
					
					byte[] bytes = new byte[3];
					bufferedInputStream.mark(3);
					bufferedInputStream.read(bytes);
					bufferedInputStream.reset();
					
					if (isSslTlsHandshake(bytes)) {
				        isSecure = true;
						beginSSL(firstHeader.getHostName());
					}
			        
			        firstHeader = httpIn.readRequestHeader(isSecure);
			        processHttp(firstHeader, isSecure);
				} catch (MissingRootCertificateException e) {
					// Unluckily Firefox and Internet Explorer will not show this message.
					// We should find a way to let the browsers display this error message.
					// May we can redirect to some kind of ZAP custom error page.

					final HttpMessage errmsg = new HttpMessage(firstHeader);
					Response.setError(errmsg, BAD_GATEWAY_RESPONSE_STATUS, e, "ZAP SSL Error");

					Response.writeHttp(errmsg, httpOut);

			        throw new IOException(e);
				}
			} else {
				processHttp(firstHeader, isSecure);
			}
	    } catch (SocketTimeoutException e) {
        	// ZAP: Log the exception
	    	if (firstHeader != null) {
	    		log.warn("Timeout accessing " + firstHeader.getURI());
	    	} else {
	    		log.warn("Timeout", e);
	    	}
	    } catch (HttpMalformedHeaderException e) {
	    	log.warn("Malformed Header: ", e);
		} catch (HttpException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
		    log.debug("IOException: ", e);
		} finally {
            proxyThreadList.remove(thread);

            // ZAP: do only close if flag is false
            if (!keepSocketOpen) {
            	disconnect();
    		}
		}
	}
	
	protected void disconnect() {
		try {
            if (httpIn != null) {
                httpIn.close();
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        }
        
        try {
            if (httpOut != null) {
                httpOut.close();
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        }

    	HttpUtil.closeSocket(inSocket);
        
		if (httpSender != null) {
            httpSender.shutdown();
        }

	}
	
	protected void processHttp(HttpRequestHeader requestHeader, boolean isSecure) throws IOException {

		HttpRequestBody reqBody = null; // ZAP: Replaced the class HttpBody with the class HttpRequestBody.
		boolean isFirstRequest = true;
		HttpMessage msg = null;
        
        // reduce socket timeout after first read
        inSocket.setSoTimeout(2500);
        
		do {

			if (isFirstRequest) {
				isFirstRequest = false;
			} else {
			    try {
			        requestHeader = httpIn.readRequestHeader(isSecure);

			    } catch (SocketTimeoutException e) {
		        	// ZAP: Log the exception
			        if (log.isDebugEnabled()) {
			            log.debug("Timed out while reading a new HTTP request.");
			        }
		        	return;
			    }
			}

			if (API.getInstance().handleApiRequest(requestHeader, httpIn, httpOut, isRecursive(requestHeader))) {
				// It was an API request
				return;
			}

			msg = new HttpMessage();
			msg.setRequestHeader(requestHeader);
			
			if (msg.getRequestHeader().getContentLength() > 0) {
				reqBody		= httpIn.readRequestBody(requestHeader); // ZAP: Changed to call the method readRequestBody.
				msg.setRequestBody(reqBody);
			}
            
			if (proxyParam.isRemoveUnsupportedEncodings()) {
				removeUnsupportedEncodings(msg);
			}

            if (isProcessCache(msg)) {
                continue;
            }
          
//            System.out.println("send required: " + msg.getRequestHeader().getURI().toString());
            
			if (parentServer.isSerialize()) {
			    semaphore = semaphoreSingleton;
			} else {
			    semaphore = this;
			}
			
			boolean send = true;
			synchronized (semaphore) {
			    
				
			    if (notificationHttp.get("OverrideListenersRequestSend").notify(this.parentServer, msg)) {
			        send = false;
			    } else if (! notificationHttp.get("ListenerRequestSend").notify(this.parentServer, msg)) {
		        	// One of the listeners has told us to drop the request
			    	return;
			    }
			    
			    try {
//					bug occur where response cannot be processed by various listener
//			        first so streaming feature was disabled		        
//					getHttpSender().sendAndReceive(msg, httpOut, buffer);
			        if (send) {
					    if (msg.getResponseHeader().isEmpty()) {
					    	// Normally the response is empty.
					    	// The only reason it wont be is if a script or other ext has deliberately 'hijacked' this request
					    	// We dont jsut set send=false as this then means it wont appear in the History tab
					    	getHttpSender().sendAndReceive(msg);
					    }

						decodeResponseIfNeeded(msg);

			             if (!notificationHttp.get("OverrideListenersResponseReceived").notify(this.parentServer, msg)) {
                            if (!notificationHttp.get("ListenerResponseReceive").notify(this.parentServer, msg)) {
                                // One of the listeners has told us to drop the response
                                return;
                            }
                        }
			        }
		        
			        
//			        notifyWrittenToForwardProxy();
			    } catch (HttpException e) {
//			    	System.out.println("HttpException");
			    	throw e;
			    } catch (SocketTimeoutException e) {
					String message = Constant.messages.getString(
							"proxy.error.readtimeout",
							msg.getRequestHeader().getURI(),
							connectionParam.getTimeoutInSecs());
					log.warn(message);
					Response.setError(msg, GATEWAY_TIMEOUT_RESPONSE_STATUS, message);
					
					notificationHttp.get("ListenerResponseReceive").notify(this.parentServer, msg);
					
			    } catch (IOException e) {
			    	Response.setError(msg, BAD_GATEWAY_RESPONSE_STATUS, e);
			    	
			    	notificationHttp.get("ListenerResponseReceive").notify(this.parentServer, msg);

			        //throw e;
			    }

				try {
					Response.writeHttp(msg, httpOut);
				} catch (IOException e) {
					StringBuilder strBuilder = new StringBuilder(200);
					strBuilder.append("Failed to write/forward the HTTP response to the client: ");
					strBuilder.append(e.getClass().getName());
					if (e.getMessage() != null) {
						strBuilder.append(": ").append(e.getMessage());
					}
					log.warn(strBuilder.toString());
				}
			}	// release semaphore
			
			ZapGetMethod method = (ZapGetMethod) msg.getUserObject();		
			
			keepSocketOpen = ((PersistentConnectionListenerNotifier) notificationHttp.get("PersistentConnectionListener")).notify(this.parentServer, msg, inSocket, method);
			
			if (keepSocketOpen) {
				// do not wait for close
				break;			
			}
	    } while (!isConnectionClose(msg) && !inSocket.isClosed());
		
    }

    protected boolean isProcessCache(HttpMessage msg) throws IOException {
        if (!parentServer.isEnableCacheProcessing()) {
            return false;
        }
        
        if (parentServer.getCacheProcessingList().isEmpty()) {
            return false;
        }
        
        CacheProcessingItem item = parentServer.getCacheProcessingList().get(0);
        if (msg.equals(item.message)) {
            HttpMessage newMsg = item.message.cloneAll();
            msg.setResponseHeader(newMsg.getResponseHeader());
            msg.setResponseBody(newMsg.getResponseBody());

            Response.writeHttp(msg, httpOut);
            
            return true;
            
        } else {

            try {
                RecordHistory history = Model.getSingleton().getDb().getTableHistory().getHistoryCache(item.reference, msg);
                if (history == null) {
                    return false;
                }
                
                msg.setResponseHeader(history.getHttpMessage().getResponseHeader());
                msg.setResponseBody(history.getHttpMessage().getResponseBody());

                Response.writeHttp(msg, httpOut);
//                System.out.println("cached:" + msg.getRequestHeader().getURI().toString());
                
                return true;
                
            } catch (Exception e) {
                return true;
            }
            
        }
        
        
//        return false;
        
    }
    
	protected HttpSender getHttpSender() {

	    if (httpSender == null) {
		    httpSender = new HttpSender(connectionParam, true, HttpSender.PROXY_INITIATOR);
		}

	    return httpSender;
	}    

	
	/**
	 * @param targethost the host where you want to connect to
	 * @throws IOException if an error occurred while establishing the SSL/TLS connection
	 */
	private void beginSSL(String targethost) throws IOException {
		// ZAP: added parameter 'targethost'
        try {
			inSocket = HttpSender.getSSLConnector().createTunnelServerSocket(targethost, inSocket);
        } catch (MissingRootCertificateException e) {
        	throw new MissingRootCertificateException(e); // throw again, cause will be catched later.
		} catch (Exception e) {
			// ZAP: transform for further processing 
			throw new IOException("Error while establishing SSL connection for '" + targethost + "'!", e);
		}
        
        httpIn = new HttpInputStream(inSocket);
        httpOut = new HttpOutputStream(inSocket.getOutputStream());
    }
	
	private static boolean isSslTlsHandshake(byte[] bytes) {
		if (bytes.length < 3) {
			throw new IllegalArgumentException("The parameter bytes must have at least 3 bytes.");
		}
		// Check if ContentType is handshake(22)
		if (bytes[0] == 0x16) {
			// Check if "valid" ProtocolVersion >= SSLv3 (TLSv1, TLSv1.1, ...) or SSLv2
			if (bytes[1] >= 0x03 || (bytes[1] == 0x00 && bytes[2] == 0x02)) {
				return true;
			}
		}
		return false;
	}
	
	private FilterInputStream buildStreamDecoder(String encoding, ByteArrayInputStream bais) throws IOException {
		if (encoding.equalsIgnoreCase(HttpHeader.DEFLATE)) {
			return new InflaterInputStream(bais, new Inflater(true));
		} else {
			return new GZIPInputStream(bais);
		}
	}

	private void decodeResponseIfNeeded(HttpMessage msg) {
		String encoding = msg.getResponseHeader().getHeader(HttpHeader.CONTENT_ENCODING);
		if (proxyParam.isAlwaysDecodeGzip() && encoding != null && !encoding.equalsIgnoreCase(HttpHeader.IDENTITY)) {
			encoding = Pattern.compile("^x-", Pattern.CASE_INSENSITIVE).matcher(encoding).replaceAll("");
			if (!encoding.equalsIgnoreCase(HttpHeader.DEFLATE) && !encoding.equalsIgnoreCase(HttpHeader.GZIP)) {
				log.warn("Unsupported content encoding method: " + encoding);
				return;
			}
            // Uncompress content
            try (ByteArrayInputStream bais = new ByteArrayInputStream(msg.getResponseBody().getBytes());
                 FilterInputStream fis = buildStreamDecoder(encoding, bais);
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ByteArrayOutputStream out = new ByteArrayOutputStream();) {
				int readLength;
				byte[] readBuffer = new byte[1024];
				while ((readLength = bis.read(readBuffer, 0,1024)) != -1) {
					out.write(readBuffer, 0, readLength);
				}
				msg.setResponseBody(out.toByteArray());
				msg.getResponseHeader().setHeader(HttpHeader.CONTENT_ENCODING, null);
				if (msg.getResponseHeader().getHeader(HttpHeader.CONTENT_LENGTH) != null) {
					msg.getResponseHeader().setHeader(HttpHeader.CONTENT_LENGTH, Integer.toString(out.size()));
				}
			} catch (IOException e) {
				log.error("Unable to uncompress gzip content: " + e.getMessage(), e);
			}
        }
    }

	private boolean isConnectionClose(HttpMessage msg) {
		
		if (msg == null || msg.getResponseHeader().isEmpty()) {
		    return true;
		}
		
		if (msg.getRequestHeader().isConnectionClose()) {
		    return true;
		}
				
		if (msg.getResponseHeader().isConnectionClose()) {
		    return true;
		}
        
        if (msg.getResponseHeader().getContentLength() == -1 && msg.getResponseBody().length() > 0) {
            // no length and body > 0 must terminate otherwise cannot there is no way for client browser to know the length.
            // terminate early can give better response by client.
            return true;
        }
		
		return false;
	}
	
    
	/**
	 * Tells whether or not the given {@code header} has a request to the (parent) proxy itself.
	 * <p>
	 * The request is to the proxy itself if the following conditions are met:
	 * <ol>
	 * <li>The requested port is the one that the proxy is bound to;</li>
	 * <li>The requested domain is {@link API#API_DOMAIN} or, the requested address is one of the addresses the proxy is
	 * listening to.</li>
	 * </ol>
	 *
	 * @param header the request that will be checked
	 * @return {@code true} if it is a request to the proxy itself, {@code false} otherwise.
	 * @see #isProxyAddress(InetAddress)
	 */
	private boolean isRecursive(HttpRequestHeader header) {
        try {
            if (header.getHostPort() == inSocket.getLocalPort()) {
                String targetDomain = header.getHostName();
                if (API.API_DOMAIN.equals(targetDomain)) {
                    return true;
                }

                if (isProxyAddress(InetAddress.getByName(targetDomain))) {
                    return true;
                }
            }
        } catch (Exception e) {
			// ZAP: Log exceptions
			log.warn(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Tells whether or not the given {@code address} is one of address(es) the (parent) proxy is listening to.
     * <p>
     * If the proxy is listening to any address it checks whether the given {@code address} is a local address or if it belongs
     * to a network interface. If not listening to any address, it checks if it's the one it is listening to.
     * 
     * @param address the address that will be checked
     * @return {@code true} if it is one of the addresses the proxy is listening to, {@code false} otherwise.
     * @see #isLocalAddress(InetAddress)
     * @see #isNetworkInterfaceAddress(InetAddress)
     */
    private boolean isProxyAddress(InetAddress address) {
        if (parentServer.getProxyParam().isProxyIpAnyLocalAddress()) {
            if (isLocalAddress(address) || isNetworkInterfaceAddress(address)) {
                return true;
            }
        } else if (address.equals(inSocket.getLocalAddress())) {
            return true;
        }
        return false;
    }

    /**
     * Tells whether or not the given {@code address} is a loopback, a site local or any local address.
     *
     * @param address the address that will be checked
     * @return {@code true} if the address is loopback, site local or any local address, {@code false} otherwise.
     * @see InetAddress#isLoopbackAddress()
     * @see InetAddress#isSiteLocalAddress()
     * @see InetAddress#isAnyLocalAddress()
     */
    private static boolean isLocalAddress(InetAddress address) {
        return address.isLoopbackAddress() || address.isSiteLocalAddress() || address.isAnyLocalAddress();
    }

    /**
     * Tells whether or not the given {@code address} belongs to any of the network interfaces.
     *
     * @param address the address that will be checked
     * @return {@code true} if the address belongs to any of the network interfaces, {@code false} otherwise.
     * @see NetworkInterface#getByInetAddress(InetAddress)
     */
    private static boolean isNetworkInterfaceAddress(InetAddress address) {
        try {
            if (NetworkInterface.getByInetAddress(address) != null) {
                return true;
            }
        } catch (SocketException e) {
            log.warn("Failed to check if an address is from a network interface:", e);
        }
        return false;
    }
    
    private void removeUnsupportedEncodings(HttpMessage msg) {
        String encoding = msg.getRequestHeader().getHeader(HttpHeader.ACCEPT_ENCODING);
        if (encoding == null) {
            return;
        }
        
        // No encodings supported in practise (HttpResponseBody needs to support them, which it doesn't, yet).
        msg.getRequestHeader().setHeader(HttpHeader.ACCEPT_ENCODING, null);
    }

    static boolean isAnyProxyThreadRunning() {
        return !proxyThreadList.isEmpty();
    }

}
