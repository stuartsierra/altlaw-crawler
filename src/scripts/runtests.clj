(ns runtests
  (:use clojure.test)
  (:require org.altlaw.test-download-log
            org.altlaw.test-crawler))

(run-all-tests #"^org\.altlaw\..*$")
