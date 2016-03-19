/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication9;
// disuruh adli

import java.util.List;
import javaapplication9.controller.ManufacturerJpaController;
import javaapplication9.models.Manufacturer;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
 *
 * @author PRAKTIKUM
 */
public class JavaApplication9 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        
//        ManufacturerJpaController cont = new ManufacturerJpaController(Persistence.createEntityManagerFactory("JavaApplication9PU"));
//        ProductCodeJpaController cont1 = new ProductCodeJpaController(Persistence.createEntityManagerFactory("JavaApplication9PU"));
//        ProductJpaController cont2 = new ProductJpaController(Persistence.createEntityManagerFactory("JavaApplication9PU"));
//        Manufacturer coba = cont.findManufacturer(200);
//        ProductCode a = cont1.findProductCode("BK");
//        Product b = new Product(500);
//        b.setManufacturerId(coba);
//        
//        b.setProductCode(a);
//        cont2.create(b);
//        System.out.println("hahahah");
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("JavaApplication9PU");
        ManufacturerJpaController manuJPA = new ManufacturerJpaController(emf);
        
        //kita panggil method yang kita buat
        List<Manufacturer> array = manuJPA.findManufacturerByName("Birders");
        for (Manufacturer manu : array)
        {
            System.out.println("Name : "+manu.getName());
            System.out.println("City : "+manu.getCity());
            System.out.println("Address 2 : "+manu.getAddressline2());
            System.out.println("----------------------------------");
        }
          
        
    }
    
}
