/**
 *  Copyright 2007-2008 University Of Southern California
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.workflowsim.scheduler;

import org.workflowsim.CondorVM;
import org.workflowsim.Job;
import org.workflowsim.WorkflowSimTags;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;

/**
 *
 * @author Weiwei Chen
 */
public class MaxMinScheduler extends DefaultScheduler{
    
    public MaxMinScheduler(){
        super();
    }
    
    private List hasChecked = new ArrayList<Boolean>();
    
    @Override
    public void run(){
        //FCFS
        //need to change it to be MinMin

        Log.printLine("Schedulin Cycle");
        int size = getCloudletList().size();
        hasChecked.clear();
        for(int t= 0; t< size; t++){
            boolean chk = false;
            hasChecked.add(false);
        }
        for(int i = 0; i < size; i ++){
            int maxIndex = 0;
            Cloudlet maxCloudlet = null;
            for(int j = 0; j < size; j++){
                Cloudlet cloudlet = (Cloudlet)getCloudletList().get(j);
                boolean chk = (Boolean)(hasChecked.get(j));
                if(!chk){
                    maxCloudlet = cloudlet;
                    maxIndex = j;
                    break;
                }
            }
            if(maxCloudlet==null){
                break;
            }
            
            
            for(int j = 0; j < size; j++)
            {
                Cloudlet cloudlet = (Cloudlet)getCloudletList().get(j);
                boolean chk = (Boolean)(hasChecked.get(j));
                
                if(chk){
                    continue;
                }
                        
                long length = cloudlet.getCloudletLength();
                
                if(length > maxCloudlet.getCloudletLength()){
                    maxCloudlet = cloudlet;
                    maxIndex = j;
                }
            }   
            hasChecked.set(maxIndex, true);
            
            int vmSize = getVmList().size();
            CondorVM firstIdleVm = null;//(CondorVM)getVmList().get(0);
            for(int j = 0; j < vmSize; j++){
                CondorVM vm = (CondorVM)getVmList().get(j);
                if(vm.getState()==WorkflowSimTags.VM_STATUS_IDLE)
                {
                    firstIdleVm = vm;
                    break;
                }
            }
            if(firstIdleVm == null){
                break;
            }
            for(int j = 0; j < vmSize; j++){
                CondorVM vm = (CondorVM)getVmList().get(j);
                if((vm.getState()==WorkflowSimTags.VM_STATUS_IDLE)
                        && vm.getCurrentRequestedTotalMips()>firstIdleVm.getCurrentRequestedTotalMips())
                {
                    firstIdleVm = vm;
                    
                }
            }
            firstIdleVm.setState(WorkflowSimTags.VM_STATUS_BUSY);
            maxCloudlet.setVmId(firstIdleVm.getId());
            getScheduledList().add(maxCloudlet);
            Log.printLine("Schedules " + maxCloudlet.getCloudletId() + " with "
                    + maxCloudlet.getCloudletLength() + " to VM " + firstIdleVm.getId() 
                    +" with " + firstIdleVm.getCurrentRequestedTotalMips());
//            if(minCloudlet.getCloudletId()==10)
//                Log.printLine();

            
        }
    }
}
