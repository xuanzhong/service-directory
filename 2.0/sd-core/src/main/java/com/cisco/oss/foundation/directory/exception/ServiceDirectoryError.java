/**
 * Copyright 2014 Cisco Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cisco.oss.foundation.directory.exception;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * The official ServiceDirectory ERROR.
 *
 * This error will throw to upper Application in Exception. Application can do recovering
 * according to the ExceptionCode if required.
 *
 *
 */
public class ServiceDirectoryError {

    /**
     * The ExceptionCode.
     */
    private ErrorCode exceptionCode;

    private String message;

    /**
     * Default constructor for JSON serializer.
     */
    public ServiceDirectoryError(){

    }

    /**
     * Constructor.
     *
     * @param ec
     *         the ExceptionCode.
     */
    public ServiceDirectoryError(ErrorCode ec) {
        this.exceptionCode = ec;
    }

    /**
     * Constructor.
     *
     * @param ec
     *         the ExceptionCode.
     * @param message
     *         the error extra message
     */
    public ServiceDirectoryError(ErrorCode ec, String message) {
        this.exceptionCode = ec;
        this.message = message;
    }

    /**
     * Get the locale-specific error message.
     *
     * @return
     *         the error message String.
     */
    @JsonIgnore
    public String getErrorMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(exceptionCode.getCode()).append(":").append(exceptionCode.getMessage());
        if(this.message != null && ! this.message.isEmpty()){
            sb.append(" - ").append(message);
        }
        return sb.toString();
    }

    /**
     * Get the ExceptionCode of the error.
     *
     * @return
     *         the ExceptionCode.
     */
    public ErrorCode getExceptionCode(){
        return exceptionCode;
    }
}
