(ns org.altlaw.test-crawler
  (:use clojure.test
        org.altlaw.crawler)
  (:require org.altlaw.test-download-log)
  (:import (org.apache.commons.codec.binary Base64)))

(use-fixtures :once org.altlaw.test-download-log/log-file-fixture)

(deftest t-crawl
  (let [resp (crawl "http://www.example.com/")]
    (is (= 200 (:response_status_code resp)))
    (is (some #{"Date"} (map first (:response_headers resp))))
    (is (.contains (String. (Base64/decodeBase64
                             (.getBytes (:response_body_base64 resp)
                                        "US-ASCII"))
                            "UTF-8")
                   "Example Web Page"))))
