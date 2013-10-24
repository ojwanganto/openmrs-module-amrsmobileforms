package org.openmrs.module.amrsmobileforms.groovy

import groovy.xml.StreamingMarkupBuilder

public class ParseXML{

    def getDirFileListing(String pathToFolder){

        new File(pathToFolder).eachFile() { file->

            def fullFilePath = pathToFolder +"/" + file.getName()
            def fileData = new XmlSlurper(false,false).parse(fullFilePath);

            if(fileData==null){
                //todo: handle blank files
            }
            else{


                def individuals = fileData.household.individuals.individual

                individuals.each{
                    String patient_number = it.patient."patient.medical_record_number"
                    patient_number = patient_number.toString()

                    if(patient_number){

                        patient_number.trim()

                        String newNum =patient_number.replaceAll("\\s","")

                        if(!patient_number.equals(newNum)){

                            it.patient."patient.medical_record_number".replaceBody(newNum)

                            def writable = new StreamingMarkupBuilder().bind { mkp.yield fileData }
                            writable.writeTo(new PrintWriter(new FileWriter(fullFilePath)))


                        }


                    }
                    else{

                        //todo:add code to perfom any  task for such a scenario

                    }

                }


            }


        }



    }


}


