/*
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

'use strict';
/**
 * Write your transction processor functions here
 */

/**
 * Update Student Data processor function.
 * @param {org.example.identity.UpdateStudentData} tx The student update data instance.
 * @transaction
 */
async function updateStudentData(tx) {
    // Save the old value of the asset.
    //const oldValue = tx.stu.email;
	const oldFirstName = tx.sd.firstName;
  const oldLastName = tx.sd.lastName;
  const oldContact = tx.sd.contact;
  
  //create Event object
  const changedData = getFactory().newEvent('org.example.identity','UserDataChanged');
  changedData.firstNameChange = [];
  changedData.lastNameChange = [];
  changedData.contactChange = [];
  
  if(tx.newFirstName!=null&&tx.newFirstName!=undefined&&tx.newFirstName!=""){
  	tx.sd.firstName = tx.newFirstName;
    changedData.firstNameChange.push(oldFirstName);
    changedData.firstNameChange.push(tx.newFirstName);
  }else{
  	changedData.firstNameChange.push(oldFirstName);
    changedData.firstNameChange.push(oldFirstName);
  }
  
  if(tx.newLastName!=null||tx.newLastName!=undefined||tx.newLastName!=""){
  	tx.sd.lastName = tx.newLastName;
    changedData.lastNameChange.push(oldLastName);
    changedData.lastNameChange.push(tx.newLastName);
  }else{
  	changedData.lastNameChange.push(oldLastName);
    changedData.lastNameChange.push(oldLastName);
  }
  
  if(tx.newContact!=null||tx.newContact!=undefined||tx.newContact!=""){
  	tx.sd.contact = tx.newContact;
    changedData.contactChange.push(oldContact);
    changedData.contactChange.push(tx.newContact);
  }else{
  	changedData.contactChange.push(oldContact);
    changedData.contactChange.push(oldContact);
  }
  
  emit(changedData);
 
    // Get the asset registry for the asset.
    const assetRegistry = await getAssetRegistry('org.example.identity.StudentDetails');
  	//const pr = await getParticipantRegistry('org.example.identity.Student');
  	//await pr.update(tx.sd.owner); // update innermost first
    await assetRegistry.update(tx.sd);
  	

}

/**
 * Linking different emails to a person.
 * @param {org.example.identity.AddDigitalIdentity} tx
 * @transaction
 */
async function addDigitalIdentity(tx){
  tx.sd.digitalIdentities.push(tx.newDigitalIdentity);
  
  const assetRegistry = await getAssetRegistry('org.example.identity.StudentDetails');
  await assetRegistry.update(tx.sd);
}


/**
 * Adding events that the person has attended person.
 * @param {org.example.identity.AddEvent} tx
 * @transaction
 */
async function addEvent(tx){
  tx.sd.events.push(tx.newEvent);
  
  const assetRegistry = await getAssetRegistry('org.example.identity.StudentDetails');
  await assetRegistry.update(tx.sd);
}

/**
 * Update check Student processor function.
 * @param {org.example.identity.checkStudent} tx The check student instance.
 * @transaction
 */
async function checkStudent(tx) {
	const assetRegistry = await getAssetRegistry('org.example.identity.StudentDetails');
  	let result = await assetRegistry.exists(tx.studentID);
  	
  //console.log("-----------------/n"+result);
  	return result;
}

/**
 * Get student data function.
 * @param {org.example.identity.getStudentData} tx .
 * @transaction
 */
async function getStudentData(tx){
	const assetRegistry = await getAssetRegistry('org.example.identity.StudentDetails');
  
  	let exists = await assetRegistry.exists(tx.studentID);
  	var details = "";
  	if(exists){
      	const logData = getFactory().newEvent('org.example.identity','QueriedData');
      	//emit event to log data in history
      	logData.forEvent = tx.event;
      	logData.queriedStudentID = tx.studentID;
      	emit(logData);
  		details = await query('GetStudentDataForGivenID',{inputValue:tx.studentID});
  		//console.log(JSON.stringify(details[0]));
      	return JSON.stringify(details[0]);
    }else{
      	//console.log(details);
    	return details;
    }
  	
  	
}

/**
 * Get history of transactions.
 * @param {org.example.identity.getHistory} tx .
 * @transaction
 */
/*
async function getHistory(tx){
	let history = await query('GetHistory');
  	console.log(history);	
  	let historyString = "";
  	for(var tx in history ){
    	historyString += JSON.stringify(tx.transactionType);
      	historyString +="\n";
    }
  
  console.log(historyString);
  	
}
*/



