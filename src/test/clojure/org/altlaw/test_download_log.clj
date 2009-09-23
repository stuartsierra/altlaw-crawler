(ns org.altlaw.test-download-log
  (:use clojure.test
        org.altlaw.crawler.download-log)
  (:import (java.io File)))

(defn log-file-fixture [f]
  (let [tmpfile (File/createTempFile "download_log" "clj")]
    (binding [download-log-file (fn [] tmpfile)]
      (f))))

(use-fixtures :once log-file-fixture)

(deftest t-not-downloaded
  (is (false? (downloaded? (str (gensym "url"))))))

(deftest t-downloaded
  (let [url (str (gensym "url"))]
    (log-download url)
    (is (true? (downloaded? url)))))

(deftest t-save-load
  (let [url (str (gensym "url"))]
    (is (false? (downloaded? url)))
    (log-download url)
    (save-download-log)
    (reload-download-log)
    (is (true? (downloaded? url)))))