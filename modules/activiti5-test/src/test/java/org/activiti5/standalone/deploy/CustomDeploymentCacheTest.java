/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti5.standalone.deploy;

import java.text.MessageFormat;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentProperties;
import org.activiti5.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti5.engine.impl.test.ResourceActivitiTestCase;

/**
 * @author Joram Barrez
 */
public class CustomDeploymentCacheTest extends ResourceActivitiTestCase {
  
  public CustomDeploymentCacheTest() {
    super("org/activiti5/standalone/deploy/custom.deployment.cache.test.activiti.cfg.xml");
  }
  
  public void testCustomDeploymentCacheUsed() {
    ProcessEngineConfigurationImpl activiti5Config = (ProcessEngineConfigurationImpl) processEngineConfiguration.getActiviti5CompatibilityHandler().getRawProcessConfiguration();
    CustomDeploymentCache customCache = (CustomDeploymentCache) activiti5Config.getProcessDefinitionCache();
    assertNull(customCache.getCachedProcessDefinition());

    String processDefinitionTemplate = DeploymentCacheTestUtil.readTemplateFile("/org/activiti5/standalone/deploy/deploymentCacheTest.bpmn20.xml");
    for (int i = 1; i <= 5; i++) {
      repositoryService.createDeployment()
              .addString("Process " + i + ".bpmn20.xml", MessageFormat.format(processDefinitionTemplate, i))
              .deploymentProperty(DeploymentProperties.DEPLOY_AS_ACTIVITI5_PROCESS_DEFINITION, Boolean.TRUE)
              .deploy();
      
      assertNotNull(customCache.getCachedProcessDefinition());
    }
    
    // Cleanup
    for (Deployment deployment : repositoryService.createDeploymentQuery().list()) {
      repositoryService.deleteDeployment(deployment.getId(), true);
    }
  }
  
}
