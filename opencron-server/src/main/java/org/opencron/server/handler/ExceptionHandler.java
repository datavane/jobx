/**
 * Copyright 2016 benjobs
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.opencron.server.handler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencron.common.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;


public class ExceptionHandler implements HandlerExceptionResolver {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception ex) {
		if (ex instanceof MaxUploadSizeExceededException) {
			WebUtils.writeJson(httpServletResponse,"长传的文件大小超过"+((MaxUploadSizeExceededException)ex).getMaxUploadSize() + "字节限制,上传失败!");
			return null;
		}
		ModelAndView view = new ModelAndView();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ex.printStackTrace(new PrintStream(byteArrayOutputStream));
		String exception = byteArrayOutputStream.toString();

		view.getModel().put("error","URL:"+ WebUtils.getWebUrlPath(httpServletRequest)+httpServletRequest.getRequestURI()+"\r\n\r\nERROR:"+exception);
		logger.error("[opencron]error:{}",ex.getLocalizedMessage());
		view.setViewName("/error/500");
		return view;
	}

}
