/*
 * Copyright (c) 2018 UTStarcom, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.utstar.study.impl;

import java.util.Collection;
import java.util.concurrent.Future;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.study.rev150105.DataModel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.study.rev150105.DataModelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.study.rev150105.GreetInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.study.rev150105.GreetMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.study.rev150105.GreetOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.study.rev150105.GreetOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.study.rev150105.StudyService;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyProvider implements StudyService, DataTreeChangeListener<DataModel> {

	private static final Logger LOG = LoggerFactory.getLogger(StudyProvider.class);

	private static final InstanceIdentifier<DataModel> DATAMODDULE_IID = InstanceIdentifier.builder(DataModel.class)
			.build();

	private final DataBroker dataBroker;
	private NotificationPublishService notificationPublishService;

	@SuppressWarnings("unchecked")
	public StudyProvider(final DataBroker dataBroker) {
		this.dataBroker = dataBroker;
		// 向dataStore注册监听
		DataTreeIdentifier<DataModel> dataTreeIdentifier = new DataTreeIdentifier<>(LogicalDatastoreType.CONFIGURATION,
				DATAMODDULE_IID);
		dataBroker.registerDataTreeChangeListener(dataTreeIdentifier, (DataTreeChangeListener) this);
	}

	/**
	 * Method called when the blueprint container is created.
	 */
	public void init() {
		LOG.info("StudyProvider Session Initiated");
	}

	/**
	 * Method called when the blueprint container is destroyed.
	 */
	public void close() {
		LOG.info("StudyProvider Closed");
	}

	@Override
	public Future<RpcResult<GreetOutput>> greet(GreetInput input) {
		GreetOutputBuilder greetOutputBuilder = new GreetOutputBuilder();
		greetOutputBuilder.setLfOutput(input.getLfInput());

		// 将数据写入dataStore
		LOG.info("Put the DataModel operational data into the MD-SAL data store");
		DataModelBuilder dataModelBuilder = (DataModelBuilder) input;
		ReadWriteTransaction wt = dataBroker.newReadWriteTransaction();
		wt.put(LogicalDatastoreType.OPERATIONAL, DATAMODDULE_IID, dataModelBuilder.build());
		wt.submit();

		return RpcResultBuilder.success(greetOutputBuilder.build()).buildFuture();
	}

	@Override
	public void onDataTreeChanged(Collection<DataTreeModification<DataModel>> changed) {
		for (DataTreeModification<DataModel> change : changed) {
			DataObjectModification<DataModel> rootNode = change.getRootNode();
			switch (rootNode.getModificationType()) {
			case WRITE:
				LOG.info("Write - before : {} after : {} ", rootNode.getDataBefore(), rootNode.getDataAfter());
				// 将数据变化转换为通知发布
				try {
					GreetMessageBuilder gmb = (GreetMessageBuilder) rootNode.getDataAfter();
					notificationPublishService.putNotification(gmb.build());
				} catch (InterruptedException e) {
					LOG.error("Convert data changes to notification publish error");
				}
				break;
			case SUBTREE_MODIFIED:
				LOG.info("Subtree_modified - before : {} after : {}", rootNode.getDataBefore(),
						rootNode.getDataAfter());
				// 将数据变化转换为通知发布
				try {
					GreetMessageBuilder gmb = (GreetMessageBuilder) rootNode.getDataAfter();
					notificationPublishService.putNotification(gmb.build());
				} catch (InterruptedException e) {
					LOG.error("Convert data changes to notification publish error");
				}
				break;
			case DELETE:
				LOG.info("Delete - before : {} after : {}", rootNode.getDataBefore(), rootNode.getDataAfter());
				// 将数据变化转换为通知发布
				try {
					GreetMessageBuilder gmb = (GreetMessageBuilder) rootNode.getDataAfter();
					notificationPublishService.putNotification(gmb.build());
				} catch (InterruptedException e) {
					LOG.error("Convert data changes to notification publish error");
				}
				break;
			}
		}

	}
}