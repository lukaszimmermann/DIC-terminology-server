package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.metadatacenter.terms.TerminologyService;
import org.metadatacenter.terms.domainObjects.OntologyClass;
import org.metadatacenter.terms.domainObjects.Relation;
import org.metadatacenter.terms.customObjects.SearchResults;
import org.metadatacenter.terms.domainObjects.Value;
import org.metadatacenter.terms.domainObjects.ValueSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import utils.Utils;

import javax.ws.rs.QueryParam;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.metadatacenter.terms.util.Constants.BP_SEARCH_SCOPE_ALL;
import static org.metadatacenter.terms.util.Constants.BP_SEARCH_SCOPE_CLASSES;
import static org.metadatacenter.terms.util.Constants.BP_SEARCH_SCOPE_VALUES;
import static org.metadatacenter.terms.util.Constants.BP_SEARCH_SCOPE_VALUE_SETS;

@Api(value = "/bioportal", description = "BioPortal operations")
public class TerminologyController extends Controller
{
  private static Logger log = LoggerFactory.getLogger(TerminologyController.class);

  public static final TerminologyService termService;

  static {
    Configuration config = Play.application().configuration();
    termService = new TerminologyService(config.getInt("bioportal.connectTimeout"), config.getInt("bioportal.socketTimeout"));
  }

  @ApiOperation(
    value = "Find classes, value sets and value set items",
    //notes = "The search scope can be specified using comma separated strings",
    httpMethod = "GET")
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Success!"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 401, message = "Unauthorized"),
    @ApiResponse(code = 500, message = "Internal Server Error")})
  @ApiImplicitParams(value = {
    @ApiImplicitParam(name = "Authorization", value="Format: apikey token={your_bioportal_apikey}. "
      + "To obtain an API key, login to BioPortal and go to \"Account\" where your API key will be displayed",
      required = true, dataType = "string", paramType = "header")})

  public static Result search(
    @ApiParam(value = "Search query. Example: 'melanoma'", required = true) @QueryParam("q") String q,
    @ApiParam(value = "Comma-separated list of search scopes. Accepted values={all,classes,value_sets,values}. "
      + "Default: 'scope=all'", required = false) @QueryParam("scope") String scope,
    @ApiParam(value = "Comma-separated list of target ontologies and/or value sets. "
    + "Example: 'ontologies=CEDARVS,NCIT'. By default, all BioPortal ontologies and value sets are considered. "
    + "The value of 'scope' overrides the list of sources specified using this parameter",
    required=false) @QueryParam("sources") String sources,
    @ApiParam(value = "Integer representing the page number. Default: 'page=1'", required = false) @QueryParam("page") int page,
    @ApiParam(value = "Integer representing the size of the returned page. Default: 'pageSize=50'", required = false) @QueryParam("pagesize") int pageSize)
  {
    //log.info("Received BioPortal search request");
    try {
      if (q.isEmpty() || !Utils.isValidAuthorizationHeader(request()))
        return badRequest();
      // Review and clean scope
      List<String> scopeList = new ArrayList<String>();
      List<String> referenceScopeList = Arrays
        .asList(BP_SEARCH_SCOPE_ALL, BP_SEARCH_SCOPE_CLASSES, BP_SEARCH_SCOPE_VALUE_SETS, BP_SEARCH_SCOPE_VALUES);
      for (String s : Arrays.asList(scope.split("\\s*,\\s*"))) {
        if (!referenceScopeList.contains(s))
          return badRequest("Wrong scope value(s)");
        else
          scopeList.add(s);
      }
      // Sources list
      List<String> sourcesList = new ArrayList<String>();
      if (sources != null && sources.length()>0)
        sourcesList = Arrays.asList(sources.split("\\s*,\\s*"));
      SearchResults results = termService.search(q, scopeList, sourcesList, page, pageSize, false, true,
        Utils.getApiKeyFromHeader(request()));
      return ok((JsonNode)new ObjectMapper().valueToTree(results));

    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }

  /** Classes **/

  @ApiOperation(
    value = "Create an ontology class",
    httpMethod = "POST")
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Created"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 401, message = "Unauthorized"),
    @ApiResponse(code = 500, message = "Internal Server Error")})
  @ApiImplicitParams(value = {
    @ApiImplicitParam(name = "Authorization", value="Format: apikey token={your_bioportal_apikey}. "
      + "To obtain an API key, login to BioPortal and go to \"Account\" where your API key will be displayed",
      required = true, dataType = "string", paramType = "header"),
    @ApiImplicitParam(value="Class to be created", required = true, dataType = "org.metadatacenter.terminology.services.bioportal.domainObjects.OntologyClass", paramType = "body")})
  // TODO: specify OntologyClass required parameters
  public static Result createProvisionalClass()
  {
    if (!Utils.isValidAuthorizationHeader(request()))
      return badRequest();
    ObjectMapper mapper = new ObjectMapper();
    OntologyClass c = mapper.convertValue(request().body().asJson(), OntologyClass.class);
    try {
      OntologyClass createdClass = termService.createProvisionalClass(c, Utils.getApiKeyFromHeader(request()));
      return created((JsonNode)mapper.valueToTree(createdClass));
    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }

  public static Result findProvisionalClass(String id)
  {
    if (id.isEmpty() || !Utils.isValidAuthorizationHeader(request()))
      return badRequest();
    try {
      OntologyClass c = termService.findProvisionalClass(id, Utils.getApiKeyFromHeader(request()));
      return ok((JsonNode)new ObjectMapper().valueToTree(c));
    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }

  public static Result findAllProvisionalClasses(String ontology)
  {
    if (!Utils.isValidAuthorizationHeader(request()))
      return badRequest();
    if (ontology.length() == 0)
      ontology = null;
    try {
      List<OntologyClass> classes = termService
        .findAllProvisionalClasses(ontology, Utils.getApiKeyFromHeader(request()));
      ObjectMapper mapper = new ObjectMapper();
      // This line ensures that @class type annotations are included for each element in the list
      ObjectWriter writer = mapper.writerFor(new TypeReference<List<OntologyClass>>(){ });
      return ok(mapper.readTree(writer.writeValueAsString(classes)));
    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }

  /** Relations **/

  public static Result createProvisionalRelation()
  {
    if (!Utils.isValidAuthorizationHeader(request()))
      return badRequest();
    ObjectMapper mapper = new ObjectMapper();
    Relation r = mapper.convertValue(request().body().asJson(), Relation.class);
    try {
      Relation createdRelation = termService.createProvisionalRelation(r, Utils.getApiKeyFromHeader(request()));
      return created((JsonNode)mapper.valueToTree(createdRelation));
    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }

  public static Result findProvisionalRelation(String id)
  {
    if (id.isEmpty() || !Utils.isValidAuthorizationHeader(request()))
      return badRequest();
    try {
      Relation r = termService.findProvisionalRelation(id, Utils.getApiKeyFromHeader(request()));
      return ok((JsonNode)new ObjectMapper().valueToTree(r));
    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }

  /** Value Sets **/

  public static Result createProvisionalValueSet()
  {
    if (!Utils.isValidAuthorizationHeader(request()))
      return badRequest();
    ObjectMapper mapper = new ObjectMapper();
    ValueSet vs = mapper.convertValue(request().body().asJson(), ValueSet.class);
    try {
      ValueSet createdVs = termService.createProvisionalValueSet(vs, Utils.getApiKeyFromHeader(request()));
      return created((JsonNode)mapper.valueToTree(createdVs));
    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }

  public static Result findProvisionalValueSet(String id)
  {
    if (id.isEmpty() || !Utils.isValidAuthorizationHeader(request()))
      return badRequest();
    try {
      ValueSet c = termService.findProvisionalValueSet(id, Utils.getApiKeyFromHeader(request()));
      return ok((JsonNode)new ObjectMapper().valueToTree(c));
    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }

  public static Result findValueSetsByVsCollection(String vsCollection)
  {
    if (!Utils.isValidAuthorizationHeader(request()))
      return badRequest();
    if ((vsCollection == null) || (vsCollection.length() == 0))
      return badRequest();
    try {
      SearchResults<ValueSet> valueSets = termService.
        findValueSetsByVsCollection(vsCollection, Utils.getApiKeyFromHeader(request()));
      ObjectMapper mapper = new ObjectMapper();
      // This line ensures that @class type annotations are included for each element in the collection
      ObjectWriter writer = mapper.writerFor(new TypeReference<SearchResults<Value>>(){ });
      return ok(mapper.readTree(writer.writeValueAsString(valueSets)));
    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }

  public static Result findValuesByValueSet(String vsId, String vsCollection)
  {
    if (!Utils.isValidAuthorizationHeader(request()))
      return badRequest();
    if ((vsId == null) || (vsId.length() == 0) || (vsCollection == null) || (vsCollection.length() == 0)  )
      return badRequest();

    try {
      vsId = URLEncoder.encode(vsId, "UTF-8");
      SearchResults<Value> values = termService.findValuesByValueSet(vsId, vsCollection, Utils.getApiKeyFromHeader(request()));
      ObjectMapper mapper = new ObjectMapper();
      // This line ensures that @class type annotations are included for each element in the collection
      ObjectWriter writer = mapper.writerFor(new TypeReference<SearchResults<Value>>(){ });
      return ok(mapper.readTree(writer.writeValueAsString(values)));
    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }

  /** Values **/
  public static Result createProvisionalValue()
  {
    if (!Utils.isValidAuthorizationHeader(request()))
      return badRequest();
    try {
      ObjectMapper mapper = new ObjectMapper();
      Value v = mapper.convertValue(request().body().asJson(), Value.class);
      Value createdValue = termService.createProvisionalValue(v, Utils.getApiKeyFromHeader(request()));
      return created((JsonNode)mapper.valueToTree(createdValue));
    } catch (HTTPException e) {
      return Results.status(e.getStatusCode());
    } catch (IOException e) {
      return internalServerError(e.getMessage());
    }
  }


}
