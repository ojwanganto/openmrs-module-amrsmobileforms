package org.openmrs.module.amrsmobileforms.web
/**
 * a class that loops through phct forms and strips off unnecessary white spaces
 * before and/or after patient.medical_record_number" value
 */
public class ParseXML{


    def cleanPatientIdentifier(){
        //todo: define path to the forms

        def filePath = "phctforms/small"

        new File(filePath).eachFile() { file->

            def fullFilePath = filePath +"/" + file.getName()
            def fileData = new XmlParser().parse(fullFilePath);


            if(fileData==null){
               //todo: add code to handle blank files
            }
            else{

                def individuals = fileData.household.individuals.individual

                individuals.each{ it ->
                    String patient_number = it.patient."patient.medical_record_number".text()

                    if(patient_number){

                        String cleanId = patient_number.trim()
                        it.patient."patient.medical_record_number".value = cleanId

                        def writer = new FileWriter(fullFilePath)

                        def printer = new XmlNodePrinter(new PrintWriter(writer))
                        printer.preserveWhitespace = true
                        printer.print(fileData)

                    }
                    else{

                      //todo:add code to perfom any  task for such a scenario

                    }


                }


            }


        }



    }





}

def xmlObj = new ParseXML();

xmlObj.cleanPatientIdentifier();
