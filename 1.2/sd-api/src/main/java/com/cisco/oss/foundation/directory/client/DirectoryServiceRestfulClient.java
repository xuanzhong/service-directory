/**
 * Copyright 2014 Cisco Systems, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cisco.oss.foundation.directory.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.oss.foundation.directory.ServiceDirectory;
import com.cisco.oss.foundation.directory.entity.InstanceChange;
import com.cisco.oss.foundation.directory.entity.ModelMetadataKey;
import com.cisco.oss.foundation.directory.entity.ModelService;
import com.cisco.oss.foundation.directory.entity.ModelServiceInstance;
import com.cisco.oss.foundation.directory.entity.OperationResult;
import com.cisco.oss.foundation.directory.entity.OperationalStatus;
import com.cisco.oss.foundation.directory.entity.ProvidedServiceInstance;
import com.cisco.oss.foundation.directory.entity.ServiceInstanceHeartbeat;
import com.cisco.oss.foundation.directory.exception.ErrorCode;
import com.cisco.oss.foundation.directory.exception.ServiceDirectoryError;
import com.cisco.oss.foundation.directory.exception.ServiceException;
import com.cisco.oss.foundation.directory.utils.HttpResponse;
import com.cisco.oss.foundation.directory.utils.HttpUtils;
import com.cisco.oss.foundation.directory.utils.HttpUtils.HttpMethod;
import com.cisco.oss.foundation.directory.utils.JsonSerializer;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_MULT_CHOICE;
import static java.net.HttpURLConnection.HTTP_OK;
import static com.cisco.oss.foundation.directory.ServiceDirectory.getServiceDirectoryConfig;
import static com.cisco.oss.foundation.directory.utils.JsonSerializer.deserialize;

/**
 * This is the client object to invoke the remote service in ServiceDirectory Server Node.
 *
 *  It implements the HTTP transportation for ServiceDirectoryService,
 *  and hides the HTTPClient details from the upper application layer.
 *
 *
 */
public class DirectoryServiceRestfulClient implements DirectoryServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryServiceRestfulClient.class);

    /**
     * The http client read timeout property.
     */
    public static final String SD_API_HTTPCLIENT_READ_TIMEOUT_PROPERTY = "com.cisco.oss.foundation.directory.httpclient.read.timeout";

    /**
     * The http client default read timeout value.
     */
    public static final int SD_API_HTTPCLIENT_READ_TIMEOUT_DEFAULT = 5;

    /**
     * The Service Directory server FQDN property name.
     */
    public static final String SD_API_SD_SERVER_FQDN_PROPERTY = "com.cisco.oss.foundation.directory.server.fqdn";

    /**
     * The default Service Directory server FQDN name.
     */
    public static final String SD_API_SD_SERVER_FQDN_DEFAULT = "vcsdirsvc";

    /**
     * The Service Directory server port property name.
     */
    public static final String SD_API_SD_SERVER_PORT_PROPERTY = "com.cisco.oss.foundation.directory.server.port";

    /**
     * The default Service Directory server port.
     */
    public static final int SD_API_SD_SERVER_PORT_DEFAULT = 2013;

    /**
     * The HTTP invoker to access remote ServiceDirectory node.
     */
    private DirectoryHttpInvoker invoker;

    /**
     * Constructor.
     */
    public DirectoryServiceRestfulClient() {
        this.invoker = new DirectoryHttpInvoker();
    }


    @Override
    public void registerInstance(ProvidedServiceInstance instance) throws ServiceException{
        String body = serialize(instance);

        HttpResponse result = invoker.invoke(toInstanceUri(instance.getServiceName(), instance.getAddress()), body, 
                HttpMethod.POST, addHeader());

        if (result.getHttpCode() != HTTP_CREATED) {
            handleHttpError(result.getHttpCode());
        }
    }

    private void handleHttpError(int httpCode) throws ServiceException{
        javax.ws.rs.core.Response.Status status = javax.ws.rs.core.Response.Status.fromStatusCode(httpCode);
        if (status!=null){
             throw new ServiceException(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR,
                    "HTTP Code is not OK, code=%s, reason=[%s]", httpCode, status.getReasonPhrase());
        }else{
            throw new ServiceException(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR,
                    "Unknown HTTP Code, code=%s", httpCode);
        }
    }

    private String toInstanceUri(String serviceName, String providerAddress) {
        return "/service/" + serviceName + "/" + providerAddress;
    }

    @Deprecated
    @Override
    public void updateInstance(ProvidedServiceInstance instance) throws ServiceException{
        String body = serialize(instance);

        HttpResponse result = invoker.invoke(toInstanceUri(instance.getServiceName(), instance.getAddress()), body,
                HttpMethod.PUT, addHeader());

        if (result.getHttpCode() != HTTP_CREATED) {
            handleHttpError(result.getHttpCode());
        }
    }

    @Override
    public void updateInstanceStatus(String serviceName, String instanceAddress, OperationalStatus status, boolean isOwned) throws ServiceException{
        String uri = toInstanceUri(serviceName, instanceAddress) + "/status";

        String body = null;
        try {
            body = "status=" + URLEncoder.encode(status.toString(), "UTF-8") + "&isOwned=" + isOwned;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UTF-8 not supported. ", e);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("api-version", ServiceDirectory.getAPIVersion());

        HttpResponse result = invoker.invoke(uri, body,
                HttpMethod.PUT, headers);

        if (result.getHttpCode() != HTTP_OK) {
            handleHttpError(result.getHttpCode());
        }
    }


    @Override
    public void updateInstanceUri(String serviceName, String instanceAddress, String uri, boolean isOwned) throws ServiceException{
        String serviceUri = toInstanceUri(serviceName, instanceAddress) + "/uri";
        String body = null;
        try {
            body = "uri=" + URLEncoder.encode(uri, "UTF-8") + "&isOwned=" + isOwned;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UTF-8 not supported. ", e);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("api-version", ServiceDirectory.getAPIVersion());
        HttpResponse result = invoker.invoke(serviceUri, body,
                HttpMethod.PUT, headers);

        if (result.getHttpCode() != HTTP_OK) {
            handleHttpError(result.getHttpCode());
        }
    }

    

    @Override
    public void updateInstanceMetadata(String serviceName, String instanceAddress, Map<String, String> metadata, boolean isOwned) throws ServiceException{
        String serviceUri = toInstanceUri(serviceName, instanceAddress) + "/metadata";
        String body = null;
        try {
            String meta = new ObjectMapper().writeValueAsString(metadata);
            body = "metadata=" + URLEncoder.encode(meta, "UTF-8") + "&isOwned=" + isOwned;
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
                LOGGER.error("Exception converting map to JSON: ", e);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("api-version", ServiceDirectory.getAPIVersion());
        HttpResponse result = invoker.invoke(serviceUri, body,
                HttpMethod.PUT, headers);

        if (result.getHttpCode() != HTTP_OK) {
            handleHttpError(result.getHttpCode());
        }
    }

    @Override
    public void unregisterInstance(String serviceName, String instanceAddress, boolean isOwned) throws ServiceException{
        String uri = toInstanceUri(serviceName, instanceAddress) + "/" + isOwned;

        HttpResponse result = invoker.invoke(uri, null,
                HttpMethod.DELETE, addHeader());

        if (result.getHttpCode() != HTTP_OK) {
            handleHttpError(result.getHttpCode());
        }
    }


    @Override
    public Map<String, OperationResult<String>> sendHeartBeat(Map<String, ServiceInstanceHeartbeat> heartbeatMap) throws ServiceException{
        String body = serialize(heartbeatMap);

        HttpResponse result = invoker.invoke("/service/heartbeat", body,
                HttpMethod.PUT, addHeader());

        if (result.getHttpCode() != HTTP_OK) {
            handleHttpError(result.getHttpCode());
        }

        return deserialize(
                result.getRetBody(), new TypeReference<Map<String, OperationResult<String>>>() {
                });

    }


    @Override
    public ModelService lookupService(String serviceName) throws ServiceException{
        HttpResponse result = invoker.invoke("/service/" + serviceName, null, HttpMethod.GET, addHeader());

        if (result.getHttpCode() != HTTP_OK) {
            handleHttpError(result.getHttpCode());
        }

        return deserialize(result.getRetBody(), ModelService.class);
    }


    @Override
    public List<ModelServiceInstance> getAllInstances() throws ServiceException{
        HttpResponse result = invoker.invoke("/service", null, HttpMethod.GET, addHeader());

        if (result.getHttpCode() != HTTP_OK) {
            handleHttpError(result.getHttpCode());
        }

        return deserialize(result.getRetBody(), new TypeReference<List<ModelServiceInstance>>() {
        });
    }


    @Override
    public ModelMetadataKey getMetadataKey(String keyName) throws ServiceException{
        HttpResponse result = invoker.invoke("/metadatakey/" + keyName, null, HttpMethod.GET, addHeader());

        if (result.getHttpCode() != HTTP_OK) {
            handleHttpError(result.getHttpCode());
        }

        return deserialize(
                result.getRetBody(), ModelMetadataKey.class);
    }


    @Override
    public Map<String, OperationResult<ModelService>> getChangedServices(Map<String, ModelService> services) throws ServiceException{
        String body = serialize(services);

        HttpResponse result = invoker.invoke("/service/changing", body, HttpMethod.POST, addHeader());

        if (result.getHttpCode() != HTTP_OK) {
            handleHttpError(result.getHttpCode());
        }

        return deserialize(
                result.getRetBody(), new TypeReference<Map<String, OperationResult<ModelService>>>() {
                });
    }

    /**
     * Deserialize a JSON String to the target object.
     *
     * @param body
     *         the JSON String.
     * @param clazz
     *         the Object class name deserialized to.
     * @return
     *         the deserialized Object instance.
     * @throws ServiceException
     */
    <T> T deserialize(String body, Class<T> clazz) {
        if (body == null || body.isEmpty()) {
            throw new ServiceException(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR,
                    ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR.getMessageTemplate(),
                    "the message body is empty");
        }

        try {
            return JsonSerializer.deserialize(body.getBytes(), clazz);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR, e,
                    ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR.getMessageTemplate(),
                    "unrecognized message, deserialize failed.");
        }
    }

    /**
     * Deserialize a JSON String to a generic object.
     *
     * This method is used when the target object is generic.
     *
     * @param body
     *         the JSON String.
     * @param typeRef
     *         the generic type.
     * @return
     *         the deserialized object instance.
     * @throws ServiceException
     */
    <T> T deserialize(String body, TypeReference<T> typeRef) {
        if (body == null || body.isEmpty()) {
            throw new ServiceException(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR,
                    ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR
                            .getMessageTemplate(), "the message body is empty");

        }

        try {
            return JsonSerializer.deserialize(body.getBytes(), typeRef);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR, e,
                    ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR.getMessageTemplate(),
                    "unrecognized message, deserialize failed.");
        }
    }

    /**
     * Serialize a object to JSON String.
     *
     * @param o
     *         the target object.
     * @return
     *         the JSON String.
     */
    String serialize(Object o) {
        String body;
        try {
            body = new String(JsonSerializer.serialize(o));
        } catch (IOException e) {
            throw new ServiceException(ErrorCode.HTTP_CLIENT_ERROR,
                    ErrorCode.HTTP_CLIENT_ERROR.getMessageTemplate(),
                    "serialize failed.");
        }
        return body;
    }

    /**
     * Keep it default for unit test.
     * @return
     *         the DirectoryInvoker
     */
    DirectoryHttpInvoker getDirectoryInvoker() {
        return invoker;
    }

    public void setInvoker(DirectoryHttpInvoker invoker) {
        this.invoker = invoker;
    }

    /**
     * It is the HTTP invoker to the ServiceDirectory Server Node.
     *
     * It wraps the complexity of HttpClient and exposes an easy method to invoke RESTful services.
     *
     *
     */
    public static class DirectoryHttpInvoker implements DirectoryInvoker {

        /* The remote ServiceDirectory node address array, in the format of http://<host>:<port> */
        public String directoryAddresses;

        /**
         * Constructor.
         *
         */
        public DirectoryHttpInvoker() {
            String sdFQDN = getServiceDirectoryConfig().getString(SD_API_SD_SERVER_FQDN_PROPERTY, SD_API_SD_SERVER_FQDN_DEFAULT);
            int port = getServiceDirectoryConfig().getInt(SD_API_SD_SERVER_PORT_PROPERTY, SD_API_SD_SERVER_PORT_DEFAULT);
            this.directoryAddresses = "https://" + sdFQDN + ":" + port;
        }

        /**
         * Invoke the HTTP RESTful Service.
         *
         * @param uri        The URI of the RESTful service.
         * @param payload    The HTTP body String.
         * @param method     The HTTP method.
         * @return
         *         the HttpResponse.
         */
        public HttpResponse invoke(String uri, String payload, HttpMethod method) {
            HttpResponse result = null;
            String url = directoryAddresses + uri;
            try {
                if (method == null || method == HttpMethod.GET) {
                    result = HttpUtils.getJson(url);
                } else if (method == HttpMethod.POST) {
                    result = HttpUtils.postJson(url, payload);
                } else if (method == HttpMethod.PUT) {
                    result = HttpUtils.putJson(url, payload);
                } else if (method == HttpMethod.DELETE) {
                    result = HttpUtils.deleteJson(url);
                }
            } catch (IOException e) {
                String errMsg = "Send HTTP Request to remote Directory Server failed";
                throw new ServiceException(ErrorCode.HTTP_CLIENT_ERROR, e, errMsg);
            } catch (ServiceException e) {
                String errMsg = "Send HTTPS Request to remote Directory Server failed";
                throw new ServiceException(ErrorCode.HTTP_CLIENT_ERROR, e, errMsg);
            }
            // HTTP_OK 200, HTTP_MULT_CHOICE 300
            if (result != null) {
                if (result.getHttpCode() < HTTP_OK || result.getHttpCode() >= HTTP_MULT_CHOICE) {
                    String errorBody = result.getRetBody();

                    if (errorBody == null || errorBody.isEmpty()) {
                        throw new ServiceException(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR,
                                ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR.getMessageTemplate(),
                                "Error Message body is empty.");
                    }
                    ServiceDirectoryError sde;
                    try {
                        sde = JsonSerializer.deserialize(errorBody.getBytes(), ServiceDirectoryError.class);
                    } catch (IOException e) {
                        String errMsg = "Deserialize error body message failed";
                        throw new ServiceException(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR, e, errMsg);
                    }

                    if (sde != null) {
                        throw new ServiceException(sde.getExceptionCode(), sde.getErrorMessage());
                    }
                }
            }
            return result;
        }

        /**
         * Invoke the HTTP RESTful Service.
         *
         * @param uri        The URI of the RESTful service.
         * @param payload    The HTTP body string.
         * @param method     The HTTP method.
         * @param headers    The HTTP headers.
         * @return
         *         the HttpResponse.
         */
        public HttpResponse invoke(String uri, String payload, HttpMethod method, Map<String, String> headers) {
            HttpResponse result = null;
            String url = directoryAddresses + uri;
            try {
                if (method == null || method == HttpMethod.GET) {
                    result = HttpUtils.getJson(url, headers);
                } else if (method == HttpMethod.POST) {
                    result = HttpUtils.postJson(url, payload, headers);
                } else if (method == HttpMethod.PUT) {
                    result = HttpUtils.put(url, payload, headers);
                } else if (method == HttpMethod.DELETE) {
                    result = HttpUtils.deleteJson(url, headers);
                }
            } catch (IOException e) {
                String errMsg = "Send HTTP Request to remote Directory Server failed";
                throw new ServiceException(ErrorCode.HTTP_CLIENT_ERROR, e, errMsg);
            } catch (ServiceException e) {
                String errMsg = "Send HTTPS Request to remote Directory Server failed";
                throw new ServiceException(ErrorCode.HTTP_CLIENT_ERROR, e, errMsg);
            }
            // HTTP_OK 200, HTTP_MULT_CHOICE 300
            if (result != null) {
                if (result.getHttpCode() < HTTP_OK || result.getHttpCode() >= HTTP_MULT_CHOICE) {
                    String errorBody = result.getRetBody();

                    if (errorBody == null || errorBody.isEmpty()) {
                        throw new ServiceException(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR,
                                ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR.getMessageTemplate(),
                                "Error Message body is empty.");
                    }
                    ServiceDirectoryError sde;
                    try {
                        sde = JsonSerializer.deserialize(errorBody.getBytes(), ServiceDirectoryError.class);
                    } catch (IOException e) {
                        String errMsg = "Deserialize error body message failed";
                        throw new ServiceException(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR, e, errMsg);
                    }

                    if (sde != null) {
                        throw new ServiceException(sde.getExceptionCode(), sde.getErrorMessage());
                    }
                }
            }
            return result;
        }
    }

    @Override
    public List<InstanceChange<ModelServiceInstance>> lookupChangesSince(String serviceName, long since) {
        HttpResponse result = invoker.invoke("/v1.2/service/changes/" + serviceName+ "/"+since, null, HttpMethod.GET, addHeader());

        if (result.getHttpCode() != HTTP_OK) {
            handleHttpError(result.getHttpCode());
        }

        return deserialize(result.getRetBody(), new TypeReference<List<InstanceChange<ModelServiceInstance>>>() {
        });
    }
    
    private Map<String, String>addHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("api-version", ServiceDirectory.getAPIVersion());
        return headers;
    }


    @Override
    public void registerInstance(ProvidedServiceInstance instance,
            boolean favorMyDC, String myDC) throws ServiceException {
        
        if (favorMyDC) {
            if (myDC.isEmpty()) {
                LOGGER.warn("Datacenter affinity is set without name.");
            } else {
                // set or update metadata
                instance.getMetadata().put("datacenter", myDC);
            }
        } else {
            LOGGER.info("Datacenter affinity is not set.");
        }
        
        registerInstance(instance);
        
    }
}

