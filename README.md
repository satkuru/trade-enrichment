# Read Me First

# Instructions: Assuming shell terminal will be used for the below.
* git clone git@github.com:satkuru/trade-enrichment.git
* cd trade-enrichment
* ./mvnw clean install
* run the follow command to bring up the application
* java -Denrich.product.static.file=data/static/product.csv  -jar target/trade-enrichment-0.0.1-SNAPSHOT.jar
* from another terminal run the following curl command, make sure to specify the correct location of the trade.csv file
* curl --request POST -F file=@data/trade/trade.csv http://localhost:80/api/v1/enrich
* test with malformed trade file, expects invalid trade record to be ignored, with error logged to the console
* curl --request POST -F file=@data/trade/trade-invalid.csv http://localhost:80/api/v1/enrich


The RestController doesn't seem support content type text/csv when called from curl.
 Only MultipartFile format request works

# Improvements/change of requirement
1. To improve better user experience and speed, Post and Get request model should be considered and avoid long poll requests.
2. The enrich post request should return an unique id after initial validation of payload.
3. The client should use the unique id to get the response of the enriched trade
4. Consider using Json payload ( convert csv to json format), which reduce the complexity in handling csv file.
5. However, output can be in json/csv based on the requirement.
6. More test coverage for controller and CSVHandler

