import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleXMLReader {

	public SimpleXMLReader(){
		
	}
	
	public Map parseValue(Element element){
		Map result = new HashMap();
		List dataList = new ArrayList();
		
		NodeList nodeList = (NodeList) element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nodeChild = (Node) nodeList.item(i);
			
			if (nodeChild.getNodeType() == Node.ELEMENT_NODE) {
				Element elementChild = (Element) nodeChild;
				String elementChildNameWithPrefix = elementChild.getTagName();
				
				int realChildLength = 0;
				NodeList c = (NodeList) element.getElementsByTagName(elementChild.getTagName());
				for(int x = 0; x < c.getLength(); x++){
					Node ch = (Node) c.item(x);
					Element elmParent = (Element) ch.getParentNode();
					if(element.getTagName().equals(elmParent.getTagName())){
						realChildLength++;
					}
				}
							
				if(realChildLength > 1){
					if(result.get(elementChild.getTagName()) == null){
						dataList = new ArrayList();
					}else{
						dataList = (List) result.get(elementChild.getTagName());
					}
					
					if(elementChild.getChildNodes().getLength() > 1){
						dataList.add(parseValue(elementChild));
						result.put(elementChild.getTagName(), dataList);
					}else{
						dataList.add(elementChild.getChildNodes().item(0).getNodeValue().trim());
						result.put(elementChild.getTagName(), dataList);
					}
				}else{
					if(elementChild.getChildNodes().getLength() >= 1 && ((Node)elementChild.getChildNodes().item(0)).getNodeType() == Node.ELEMENT_NODE){
						result.put(elementChild.getTagName(), parseValue(elementChild));
					}else{												
						if(elementChild.getChildNodes().item(0) != null){
							if(elementChild.getChildNodes().item(0).getNodeValue() != null)
								result.put(elementChild.getTagName(), elementChild.getChildNodes().item(0).getNodeValue());
							else
								result.put(elementChild.getTagName(), elementChild.getChildNodes().item(0).getTextContent());
						}else{
							result.put(elementChild.getTagName(), "");
						}
						
					}
				}
			}
		}
		
		return result;
	}
	
	public Map parseValueWithoutPrefix(Element element){
		Map result = new HashMap();
		List dataList = new ArrayList();
		
		NodeList nodeList = (NodeList) element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nodeChild = (Node) nodeList.item(i);
			
			if (nodeChild.getNodeType() == Node.ELEMENT_NODE) {
				Element elementChild = (Element) nodeChild;
				String elementChildNameWithoutPrefix = elementChild.getTagName();
				
				if(elementChildNameWithoutPrefix.contains(":")){
					elementChildNameWithoutPrefix = elementChildNameWithoutPrefix.split(":")[1];					
				}
				
				int realChildLength = 0;
				NodeList c = (NodeList) element.getElementsByTagName(elementChild.getTagName());
				for(int x = 0; x < c.getLength(); x++){
					Node ch = (Node) c.item(x);
					Element elmParent = (Element) ch.getParentNode();
					if(element.getTagName().equals(elmParent.getTagName())){
						realChildLength++;
					}
				}
				
				if(realChildLength > 1){
					if(result.get(elementChildNameWithoutPrefix) == null){
						dataList = new ArrayList();
					}else{
						dataList = (List) result.get(elementChildNameWithoutPrefix);
					}
					
					if(elementChild.getChildNodes().getLength() > 1){
						dataList.add(parseValueWithoutPrefix(elementChild));
						result.put(elementChildNameWithoutPrefix, dataList);
					}else{
						dataList.add(elementChild.getChildNodes().item(0).getNodeValue().trim());
						result.put(elementChildNameWithoutPrefix, dataList);
					}
				}else{
					if(elementChild.getChildNodes().getLength() >= 1 && ((Node)elementChild.getChildNodes().item(0)).getNodeType() == Node.ELEMENT_NODE){
						result.put(elementChildNameWithoutPrefix, parseValueWithoutPrefix(elementChild));
					}else{												
						if(elementChild.getChildNodes().item(0) != null){							
							if(elementChild.getChildNodes().item(0).getNodeValue() != null)
								result.put(elementChildNameWithoutPrefix, elementChild.getChildNodes().item(0).getNodeValue());
							else
								result.put(elementChildNameWithoutPrefix, elementChild.getChildNodes().item(0).getTextContent());
						}else{
							result.put(elementChildNameWithoutPrefix, "");
						}
						
					}
				}
			}
		}
		
		return result;
	}
	
	public Map convertXML(Document doc, String rootName){
		Map result = new HashMap();
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName(rootName);
		
		Node nNode = nList.item(0);
		
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;

			result = parseValue(eElement);
		}
		
		return result;
	}
	
	public Map convertXMLWithoutPrefix(Document doc, String rootName){
		Map result = new HashMap();
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName(rootName);
		
		Node nNode = nList.item(0);
		
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;

			result = parseValueWithoutPrefix(eElement);
		}
		
		return result;
	}
	
	public static void main(String a[]){
		try{
			File xmlFile = new File("sampleFault2.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(xmlFile);
			
			SimpleXMLReader sXML = new SimpleXMLReader();
			Map result = sXML.convertXMLWithoutPrefix(doc, "soapenv:Envelope");
			System.out.println(result);
			Map soapBody = (Map) result.get("Body");
			Map soapFault = (Map) soapBody.get("Fault");
			Map soapDetail = (Map) soapFault.get("detail");
			System.out.println(soapBody);
			System.out.println(soapFault);
			System.out.println(soapDetail);
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
