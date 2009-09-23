(ns org.altlaw.crawler.download-log
  (:require [clojure.contrib.duck-streams :as duck]
            [clojure.contrib.java-utils :as j])
  (:import (java.io File PushbackReader PrintWriter Reader)
           (java.util Date TimeZone)
           (java.text SimpleDateFormat)))

(defn #^File download-log-file []
  (j/file (System/getProperty "org.altlaw.home" ".")
          "data"
          "download_log.clj"))

(defn- read-download-log [#^Reader rdr]
  (read (PushbackReader. rdr)))

(defn- write-download-log [#^PrintWriter wtr data]
  (binding [*out* wtr]
    (pr data)))

(defn- load-download-log
  ([] (load-download-log (download-log-file)))
  ([#^File file]
     (if (and (.exists file) (pos? (.length file)))
       (with-open [rdr (duck/reader file)]
         (read-download-log rdr))
       {})))

(def #^{:private true} *download-log* (ref nil))

(defn- get-download-log []
  (if @*download-log*
    *download-log*
    (do (dosync (ref-set *download-log* (load-download-log)))
        *download-log*)))

(def #^{:private true} *iso-date-format*
     (doto (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss'Z'")
       (.setTimeZone (TimeZone/getTimeZone "GMT"))))

(defn- timestamp []
  (.format *iso-date-format* (Date.)))

(defn save-download-log
  ([] (save-download-log (download-log-file)))
  ([#^File file]
     (.mkdirs (.getParentFile file))
     (with-open [wtr (duck/writer file)]
       (write-download-log wtr @(get-download-log)))))

(defn reload-download-log []
  (dosync (alter *download-log* (load-download-log))))

(defn log-download
  "Logs a download of the URL (a String). If date (Date object or
  ISO-8601 date/time string) is not given, assume current date/time."
  [url]
  (assert (string? url))
  (dosync (alter (get-download-log)
                 assoc url (timestamp))))

(defn downloaded?
  "Returns true if the URL (a String) has already been downloaded."
  [url]
  (assert (string? url))
  (contains? @(get-download-log) url))

(defn set-not-downloaded [url]
  (assert (string? url))
  (dosync (alter (get-download-log) dissoc url)))
