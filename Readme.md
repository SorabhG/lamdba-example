A — Prepare the Lambda functions (quick recap)

You already have spring-lambda-products-1.0.0.jar and a handler class com.example.lambda.StreamLambdaHandler that extends FunctionInvoker.

Create 2 Lambda functions (copying the same JAR):

1. Create getProductsLambda

Console: Lambda → Create function

Choose Author from scratch

Name: getProductsLambda

Runtime: Java 17

Execution role: create or select a role with basic Lambda permissions

Under Code upload your target/spring-lambda-products-1.0.0.jar (Upload from .zip or .jar)

Handler: com.example.lambda.StreamLambdaHandler

Configuration → Environment variables → Edit → add:

Key: SPRING_CLOUD_FUNCTION_DEFINITION

Value: getProducts

Save.

2. Create addProductLambda

Repeat the above, but:

Name: addProductLambda

Environment variable SPRING_CLOUD_FUNCTION_DEFINITION = addProduct

Note: Both functions have the same JAR and handler. The environment variable controls which function (bean) Spring invokes.

B — Create an HTTP API in API Gateway

(HTTP API is simpler and cheaper for REST-style endpoints than REST API.)

Console: API Gateway → Create API

Choose HTTP API (not REST API). Click Build.

You’ll land on the “Create an HTTP API” flow.

C — Add the first integration (GET /products)

On Integrations step: click Add integration → Lambda.

Select the region and pick the Lambda function getProductsLambda.

Click Add integration.

(If AWS asks to grant permission for API Gateway to invoke your Lambda, accept — the console will add the resource-based permission automatically.)

D — Create the route (GET /products) and attach integration

After adding integration, go to Routes (left nav) or when prompted: Create route.

Click Create →

Method: GET

Resource path: /products

Now attach integration: choose the integration you just created (getProductsLambda).

Leave authorization as Open (for learning) or set up later.

Click Create.

E — Add the second integration (POST /product)

Repeat for POST:

Integrations → Add integration → Lambda → pick addProductLambda.

Routes → Create route:

Method: POST

Path: /product

Attach to the addProductLambda integration.

F — (Optional) Enable CORS if you’ll call from browser

If you plan to call the endpoints from a browser (JS frontend), enable CORS:

Go to route /product (or /products) → CORS → Enable CORS.

Set allowed origins, methods, headers as needed. For basic dev, you can allow * for origins.

G — Deploy / Stage (HTTP API uses $default stage)

HTTP APIs use a default stage $default automatically if you don’t create custom stages. After creating routes and integrations:

Click Deployments / Stages (or check the top-right “Invoke URL”)

Copy the Invoke URL shown (it’s like https://<api-id>.execute-api.<region>.amazonaws.com).

Your full endpoints will be:

GET https://<api-id>.execute-api.<region>.amazonaws.com/products

POST https://<api-id>.execute-api.<region>.amazonaws.com/product

H — Test the endpoints
1) Test GET /products

From your terminal:

curl -v https://<api-id>.execute-api.<region>.amazonaws.com/products


Expected: JSON array [] initially (empty) or list of products.

2) Test POST /product

Add a product:

curl -v -X POST \
https://<api-id>.execute-api.<region>.amazonaws.com/product \
-H "Content-Type: application/json" \
-d '{"name":"Laptop","price":1500.0}'


Expected response: product JSON with id, name, price.

Then call GET again to see the product in the list.