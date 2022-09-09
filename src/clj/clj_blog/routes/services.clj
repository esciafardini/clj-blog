(ns clj-blog.routes.services
  (:require
   [clj-blog.messages :as msg]
   [clj-blog.blog-posts :as blog]
   [clj-blog.middleware.formats :as formats]
   [reitit.coercion.spec :as spec-coercion]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [ring.util.http-response :as response]))

;;the save-message! and get-message-list fns in this namespace are HTTP specific
(defn get-messages
  "HTTP means of fetching messages from the psql database"
  [_]
  (response/ok (msg/get-messages)))

(defn save-message!
  "HTTP means of saving a message to the psql database"
  [{{params :body} :parameters}] ;; interesting map destructuring
  (try
    (msg/save-message! params) ;touches db
    (response/ok {:status :ok}) ;http response map
    (catch Exception e
      (let [{id :clj-blog/error-id
             errors :errors} (ex-data e)] ;;;interesting destructure here - let bindings created via keywords
        (case id
          :validation
          (response/bad-request {:errors errors})
          ;;else
          (response/internal-server-error
           {:errors {:server-error ["Failed to save message!"]}}))))))

(defn get-blog-posts
  "HTTP means of fetching blog posts from the psql database"
  [_]
  (response/ok (blog/blog-post-list)))

(defn get-blog-post-by-id
  [{{{:keys [id]} :path} :parameters}]
  (response/ok (blog/blog-post-by-id id)))

(defn service-routes
  "Reitit routes for /api with lots of middleware.  Descriptions in comments."
  []
  ["/api"
   {:middleware [parameters/parameters-middleware       ;query params, form params
                 muuntaja/format-negotiate-middleware   ;content negotiation
                 muuntaja/format-response-middleware    ;encoding response body
                 exception/exception-middleware         ;exception handling
                 muuntaja/format-request-middleware     ;decoding request body
                 coercion/coerce-response-middleware    ;coercing request parameters
                 coercion/coerce-request-middleware     ;coercing request parameters
                 multipart/multipart-middleware]        ;multipart params
    ;with muuntaja and coercion, reitit enriches middleware
    :muuntaja formats/instance ;luminus provides this custom muuntaja instance
    :coercion spec-coercion/coercion ;used by reitit's coercion middleware
    :swagger {:id ::api}} ;swagger keyword is inherited by all child routes, designates them with ID ::api
   ["" {:no-doc true}
    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]
    ["/swagger-ui*"
     {:get (swagger-ui/create-swagger-ui-handler ;Swagger UI offers the ability to visualize and interact with our services API
            {:url "/api/swagger.json"})}]]
   ["/blog-posts"
    ["" {:get
         {:responses
          {200
           {:body  ;; Data Spec for response body - provides a specification for each route's params and responses
            {:blog-list
             [{:id pos-int?
               :title string?
               :component_function string?
               :date_created inst?}]}}}
          :handler get-blog-posts}}]
    ["/by/:id"
     {:get
      {:parameters {:path {:id int?}}
       :responses
       {200
        {:body  ;; Data Spec for response body - provides a specification for each route's params and responses
         {:blog-post
          {:id pos-int?
           :title string?
           :component_function string?
           :date_created inst?}}}}
       :handler get-blog-post-by-id}}]]
   ["/messages"
    {:get
     {:responses
      {200
       {:body  ;; Data Spec for response body - provides a specification for each route's params and responses
        {:messages
         [{:id pos-int?
           :name string?
           :message string?
           :timestamp inst?}]}}}
      :handler get-messages}}]
   ["/message"
    {:post
     {:parameters
      {:body ;; Data Spec for Request body parameters
       {:name string?
        :message string?}}
      :responses
      {200
       {:body map?}
       400
       {:body map?}
       500
       {:errors map?}}
      :handler save-message!}}]])
