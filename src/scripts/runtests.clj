(ns runtests
  (:use clojure.test)
  (:require org.altlaw.test-download-log))

(run-all-tests #"^org\.altlaw\..*$")
