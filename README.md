# DataMaskerTool

Introduction

    <customer id="122336"> 
    <customer_name>Tom Smith</customer_name> 
    <address>Apt 46a</address> 
    <address>21 Main Street</address> 
    <town>Anytown</town> 
    <region>Region1</region> 
    <country>Ireland</country> 
    <telephone>123 456 789</telephone> 
    </customer>

An Example of an XML Masking Requirement

Consider the above simple example XML data - the customer_name element (as well as other elements) clearly contains sensitive personally identifiable information (PII data). In order to mask this information, the tool must reach down into the XML and modify the text between the two <customer_name> tags without changing anything else.
Functionality

DataMaskerTool is a tool that will mask the text value of the element or attribute using a predefined dataset. This dataset can be configured externally. This tool has a feature to skip masking for specific elements or attributes. These elements must be configured in a properties file. The output masked XML file will be exactly the same as the Input XML file except for the masked content.

This tool can also mask image files using a third-party tool ImageMagick.

There are many tools available in the market, but most of the tools won't generate readable content as part of masking, where this tool can do that.
Rules:

    Digit will be replaced with a Random Digit.
    Words will be replaced with the same length random word from DataSet/Dictionary.
    If the dataset does not contain a word with a specific length, then the word will be replaced with multiples of 'X' Character.
    The alphanumeric texts will be replaced with the alphanumeric text of random alphabets/digits.
    Special Characters won't be modified.
    DTD, Namespace, and Entities won't be modified.
    Structure of the Masked XML will be the same as Input XML.


Prerequisites:


    Minimum Java 8 Should be installed on the system
    java path variable should be configured
    Optional:
      ImageMagick v7.0 to be installed for Image Masking (Eg: png, jpeg, jpg)

Input for the tool:

    A Compressed ZIP file contains well-formed XML and image files
    External properties file with below information
        XML Elements & Attributes (Which will be ignored as part of masking)
        Output Directory to upload the files
        Pre-defined dataset file (If not configured default data set will be used)
        Image masking flag (Only supported png, jpeg, jpg formats)

Output :

    A Compressed Zip File with the Masked XML and image files
    
Few Properties to configure in application.properties:
-----------------------------------------------------
masker.upload-dir - Location where uploaded files will be stored (Must be configured)
masker.ignore-xmlelement - elements will be ignored by the tool

masker.enable-attributemask - Flag to enable attribute masking
masker.ignore-xmlattribute -  attributes will ignored by the tool (Only works if property masker.enable-attributemask set to true)

masker.imagemask.enabled - Flag to enable image masking
masker.imagemask.converter.path - Installed ImageMagick application path
masker.imagemask.supported-formats - list of image formats wants to mask (Only works if property masker.imagemask.enabled set to true)


Once server started succesfully
Open the Browser 
Access the below URL
http://localhost:8181/payloadmasker/index.html

Note: Only zip file allowed to upload

