package ip.ranges.remover

import ip.ranges.remover.ApplicationProperties.{elasticHost, elasticPort}
import org.apache.http.HttpHost
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.client.{RequestOptions, RestClient, RestHighLevelClient}
import org.elasticsearch.common.xcontent.XContentType

object ElasticClient {

  lazy val client = new RestHighLevelClient(
    RestClient.builder(new HttpHost(elasticHost, elasticPort, "http"))
  )

  def postData(indexName: String, data: String): IndexResponse = {
    val request = new IndexRequest(indexName)

    request.source(data, XContentType.JSON)

    val response = client.index(request, RequestOptions.DEFAULT)

    println(s"Response from Elastic: $response")
    response
  }
}
