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
							
				if(element.getElementsByTagName(elementChild.getTagName()).getLength() > 1){
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
					if(elementChild.getChildNodes().getLength() > 1){
						result.put(elementChild.getTagName(), parseValue(elementChild));
					}else{
						result.put(elementChild.getTagName(), elementChild.getChildNodes().item(0).getNodeValue().trim());
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
	
	public static void main(String a[]){
		try{
			File xmlFile = new File("sampleData.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(xmlFile);
			
			SimpleXMLReader sXML = new SimpleXMLReader();
			Map result = sXML.convertXML(doc, "soap:envelope");
			
			Map soapHeader = (Map) result.get("soap:header");
			Map soapBody = (Map) result.get("soap:body");
			List accountList = (List) soapBody.get("account");
			
			System.out.println(soapHeader);
			System.out.println(soapBody);
			System.out.println(accountList.get(0));
			System.out.println(accountList.get(1));
			System.out.println(accountList.get(2));
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
