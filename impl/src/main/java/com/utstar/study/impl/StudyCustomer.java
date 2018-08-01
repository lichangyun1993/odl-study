/*
 * Copyright (c) 2018 UTStarcom, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.utstar.study.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.study.rev150105.GreetMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.study.rev150105.StudyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyCustomer implements StudyListener {

	private static final Logger LOG = LoggerFactory.getLogger(StudyCustomer.class);

	@Override
	public void onGreetMessage(GreetMessage notification) {
		LOG.info("greetMessage" + notification.getMessage());
	}

}
