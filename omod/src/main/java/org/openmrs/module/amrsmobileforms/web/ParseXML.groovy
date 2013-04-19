import groovy.xml.StreamingMarkupBuilder
public class ParseXML{


    def getDirFileListing(){
        def filePath = "phctforms/small"

        new File(filePath).eachFile() { file->

            def fullFilePath = filePath +"/" + file.getName()
            def fileData = new XmlSlurper().parse(fullFilePath);

            if(fileData==null){
                //todo:
            }
            else{


                def individuals = fileData.household.individuals.individual
                //def petientNo = patient.medical_record_number
                individuals.each{
                    String patient_number = it.patient."patient.medical_record_number"


                    if(patient_number){

                        patient_number.trim()

                        patient_number.replaceAll(/ *$/, '')

                        it.patient."patient.medical_record_number".replaceBody(patient_number)

                        def writable = new StreamingMarkupBuilder().bind { mkp.yield fileData }
                        writable.writeTo(new PrintWriter(new FileWriter(fullFilePath)))

                        println "Patient Identifier: " + it.patient."patient.medical_record_number"

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

xmlObj.getDirFileListing();

