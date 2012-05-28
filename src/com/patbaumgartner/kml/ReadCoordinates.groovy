package com.patbaumgartner.kml;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

// Read XML File

def title = "2012 Golden Pass"

def inFileName = "2012_Golden_Pass.kml"
def outFileName = "2012_Golden_Pass_Coordinates.txt"
def kmlOutFileName = "2012_Golden_Pass_Trip_Path.kml"
def templateKmlFileName = "KmlTemplate.kml"

def inFile = new File(inFileName)
def outFile = new File(outFileName)
def kmlOutFile = new File(kmlOutFileName)
def templateKmlFile = new File(templateKmlFileName)


// read coordinates
def kml = new XmlSlurper().parse(inFile)

def coordinates = new StringBuilder()
def lastPlaceDefX
def lastPlaceDefY
def stepSize = 0.01

kml.Document.Folder.Placemark.eachWithIndex { it, index  -> 
	if(index % 1 == 0 ) {
		def point = it.Point.coordinates.text().replaceFirst("\n", "")
		def place = point.split(",")
		def placeDefX = Double.parseDouble(place[0])
		def placeDefY = Double.parseDouble(place[1])
		
		if(index == 0){
			lastPlaceDefX = placeDefX
			lastPlaceDefY = placeDefY
		}
		
		if (((lastPlaceDefX + stepSize) > placeDefX) && (placeDefX > (lastPlaceDefX - stepSize)) && 
		((lastPlaceDefY + stepSize) > placeDefY) && (placeDefY > (lastPlaceDefY - stepSize))){
			coordinates.append(point)	
		}
		lastPlaceDefX = placeDefX
		lastPlaceDefY = placeDefY
	}
}
// write coordinates to a txt file
outFile.write( coordinates.toString() )

// using a kml template
def binding = [ "title" : title , "coordinates" : coordinates.toString()  ]

def engine = new SimpleTemplateEngine()
def template = engine.createTemplate(templateKmlFile)
def writable = template.make(binding)

// write google kml template
kmlOutFile.write(writable.toString())
