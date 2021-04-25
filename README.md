**IP Ranges Intersection Remover**

**How to run it**

To run this project you need to install Spark 3.0.0, Postgresql and Elasticsearch and run main class IpRangesIntersectionRemoverBoot.
To avoid installing Postgresql and Elasticsearch you can switch off integration with this components by setting flags elastic.sendResultToElastic and database.useDataFromDatabase to false in application.properties file.

The data are stored in postgresql table IpRanges or in folder data.
The result should be visible on the standard output and can be found in elasticsearch index ip-ranges.

To run this project you can also use command "sbt run".
