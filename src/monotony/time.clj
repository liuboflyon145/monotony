(ns ^{:doc "Monotony's Time protocol"
      :author "Alex Redington"} monotony.time
      (:import java.util.Date))

(defprotocol Time
  "An instant in time."
  (period [time duration] "Return two dates which mark the start an
  end points of a period of time.")
  (millis [time] "Return the number of milliseconds since Jan 1 1970, 12:00AM GMT.")
  (date [time] "Returns a date object equivalent to time."))

(extend-protocol Time
  Long
  (millis
    [time]
    time)
  (period
    [time duration]
    [(date time) (date (+ time duration))])
  (date
    [time]
    (Date. time))
  Date
  (millis
    [time]
    (.getTime time))
  (period
    [time duration]
    [time (date (+ (millis time) duration))])
  (date
    [time]
    time))

(defmacro extend-if
  "If `class` resolves to a class, extend `protocol` to it with `specs`."
  [class protocol & specs]
  (when-let [joda-time-class (try (Class/forName (name class))
                                  (catch java.lang.ClassNotFoundException e
                                    nil))]
    `(extend-type ~joda-time-class ~protocol ~@specs)))

(extend-if org.joda.time.DateTime
           Time
           (millis [time]
                   (.getMillis time))
           (period [time duration]
                   [(date time) (date (+ (millis time) duration))])
           (date [time]
                 (.toDate time)))
