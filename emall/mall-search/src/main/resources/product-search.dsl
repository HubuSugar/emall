GET product/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "skuTitle": "华为"
          }
        }
      ],
      "filter": [
        {
          "term": {
            "catalogId": "225"
          }
        },
        {
          "terms": {
            "brandId": [
              "1",
              "2",
              "9"
            ]
          }
        },
        {
          "nested": {
            "path": "attrs",
            "query": {
              "bool": {
                "must": [
                  {
                    "term": {
                      "attrs.attrId": {
                        "value": "15"
                      }
                    }
                  },
                  {
                    "terms": {
                      "attrs.attrValue": [
                        "海思（Hisilicon）",
                        "HUAWEI Kirin 970"
                      ]
                    }
                  }
                ]
              }
            }
          }
        },
        {
          "term": {
            "hasStock": "true"
          }
        },
        {
          "range":{
            "skuPrice":{
              "gte":"100",
              "lte":"6300"
            }
          }
        }
      ]
    }
  },
   "highlight": {
      "fields": {"skuTitle": {}},
      "pre_tags": "<b style='color:red'>",
      "post_tags": "</b>"
   },
  "sort": [
    {
      "skuPrice": {
        "order": "desc"
      }
    }
  ],
  "from": 0,
  "size": 20,
  "aggs": {
      "brand_agg": {
        "terms": {
          "field": "brandId",
          "size": 10
        },
        "aggs": {
          "brandName_agg": {
            "terms": {
              "field": "brandName",
              "size": 10
            }
          },
          "brandImg_agg": {
            "terms": {
              "field": "brandImg",
              "size": 10
            }
          }
        }
      },
      "catalog_agg":{
         "terms": {
          "field": "catalogId",
          "size": 10
        },
        "aggs": {
          "catalog_name_agg": {
            "terms": {
              "field": "catalogName",
              "size": 10
            }
          }
        }
      },
      "attr_agg":{
        "nested": {
          "path": "attrs"
        },
        "aggs": {
          "attr_id_agg": {
            "terms": {
              "field": "attrs.attrId",
              "size": 10
            },
            "aggs": {
              "attr_name_agg": {
                "terms": {
                  "field": "attrs.attrName",
                  "size": 10
                }
              },
              "attr_value_agg": {
                "terms": {
                  "field": "attrs.attrValue",
                  "size": 10
                }
              }
            }
          }
        }
      }
    }
}
