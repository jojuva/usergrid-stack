/*******************************************************************************
 * Copyright 2012 Apigee Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.usergrid.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.usergrid.persistence.cassandra.CassandraService.DEFAULT_APPLICATION_ID;
import static org.usergrid.services.ServiceParameter.parameters;
import static org.usergrid.services.ServicePayload.batchPayload;
import static org.usergrid.services.ServicePayload.payload;
import static org.usergrid.utils.InflectionUtils.pluralize;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usergrid.cassandra.CassandraRunner;
import org.usergrid.persistence.Entity;
import org.usergrid.persistence.EntityManagerFactory;
import org.usergrid.persistence.cassandra.EntityManagerFactoryImpl;
import org.usergrid.test.ShiroHelperRunner;
import org.usergrid.utils.JsonUtils;

@RunWith(ShiroHelperRunner.class)
public abstract class AbstractServiceTest {
	public static final boolean USE_DEFAULT_DOMAIN = false;

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractServiceTest.class);

	protected Properties properties;
    protected EntityManagerFactoryImpl emf;
   	protected ServiceManagerFactory smf;

    @Before
	public void setupLocal() {
		emf = (EntityManagerFactoryImpl) CassandraRunner.getBean(EntityManagerFactory.class);
		smf = CassandraRunner.getBean(ServiceManagerFactory.class);
        properties = CassandraRunner.getBean("properties",Properties.class);
	}


	UUID dId = null;

	public UUID createApplication(String organizationName,
			String applicationName) throws Exception {
		if (USE_DEFAULT_DOMAIN) {
			return DEFAULT_APPLICATION_ID;
		}
		return emf.createApplication(organizationName, applicationName);
	}

	public Entity doCreate(ServiceManager sm, String entityType, String name)
			throws Exception {
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put("name", name);

		return testRequest(sm, ServiceAction.POST, 1, properties,
				pluralize(entityType)).getEntity();
	}

	public ServiceResults testRequest(ServiceManager sm, ServiceAction action,
			int expectedCount, Map<String, Object> properties, Object... params)
			throws Exception {
		ServiceResults results = invokeService(sm, action, properties, params);
		assertNotNull(results);
		assertEquals(expectedCount, results.getEntities().size());
		dumpResults(results);
		return results;
	}
	
	public ServiceResults invokeService(ServiceManager sm, ServiceAction action, Map<String, Object> properties, Object... params)
            throws Exception {
        ServiceRequest request = sm.newRequest(action, parameters(params),
                payload(properties));
        logger.info("Request: " + action + " " + request.toString());
        dumpProperties(properties);
        ServiceResults results = request.execute();
        assertNotNull(results);
        dumpResults(results);
        return results;
    }

	public ServiceResults testBatchRequest(ServiceManager sm,
			ServiceAction action, int expectedCount,
			List<Map<String, Object>> batch, Object... params) throws Exception {
		ServiceRequest request = sm.newRequest(action, parameters(params),
				batchPayload(batch));
		logger.info("Request: " + action + " " + request.toString());
		dump("Batch", batch);
		ServiceResults results = request.execute();
		assertNotNull(results);
		assertEquals(expectedCount, results.getEntities().size());
		dumpResults(results);
		return results;
	}

	public void dumpProperties(Map<String, Object> properties) {
		if (properties != null) {
			logger.info("Input:\n"
					+ JsonUtils.mapToFormattedJsonString(properties));
		}
	}

	public void dumpResults(ServiceResults results) {
		if (results != null) {
			List<Entity> entities = results.getEntities();
			dump("Results", entities);
		}
	}

	public void dumpEntity(Entity entity) {
		dump("Entity", entity);
	}

	public void dump(Object obj) {
		dump("Object", obj);
	}

	public void dump(String name, Object obj) {
		if (obj != null) {
			logger.info(name + ":\n" + JsonUtils.mapToFormattedJsonString(obj));
		}
	}

	public ServiceResults testDataRequest(ServiceManager sm,
			ServiceAction action, Map<String, Object> properties,
			Object... params) throws Exception {
		ServiceRequest request = sm.newRequest(action, parameters(params),
				payload(properties));
		logger.info("Request: " + action + " " + request.toString());
		dumpProperties(properties);
		ServiceResults results = request.execute();
		assertNotNull(results);
		assertNotNull(results.getData());
		dump(results.getData());
		return results;
	}

}
