/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication9.controller;

import java.io.Serializable;
import java.util.List;
import javaapplication9.controller.exceptions.NonexistentEntityException;
import javaapplication9.controller.exceptions.PreexistingEntityException;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javaapplication9.models.Manufacturer;
import javaapplication9.models.Product;
import javaapplication9.models.ProductCode;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author PRAKTIKUM
 */
public class ProductJpaController implements Serializable {

    public ProductJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Product product) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Manufacturer manufacturerId = product.getManufacturerId();
            if (manufacturerId != null) {
                manufacturerId = em.getReference(manufacturerId.getClass(), manufacturerId.getManufacturerId());
                product.setManufacturerId(manufacturerId);
            }
            ProductCode productCode = product.getProductCode();
            if (productCode != null) {
                productCode = em.getReference(productCode.getClass(), productCode.getProdCode());
                product.setProductCode(productCode);
            }
            em.persist(product);
            
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProduct(product.getProductId()) != null) {
                throw new PreexistingEntityException("Product " + product + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Product product) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product persistentProduct = em.find(Product.class, product.getProductId());
            Manufacturer manufacturerIdOld = persistentProduct.getManufacturerId();
            Manufacturer manufacturerIdNew = product.getManufacturerId();
            ProductCode productCodeOld = persistentProduct.getProductCode();
            ProductCode productCodeNew = product.getProductCode();
            if (manufacturerIdNew != null) {
                manufacturerIdNew = em.getReference(manufacturerIdNew.getClass(), manufacturerIdNew.getManufacturerId());
                product.setManufacturerId(manufacturerIdNew);
            }
            if (productCodeNew != null) {
                productCodeNew = em.getReference(productCodeNew.getClass(), productCodeNew.getProdCode());
                product.setProductCode(productCodeNew);
            }
            product = em.merge(product);
            if (manufacturerIdOld != null && !manufacturerIdOld.equals(manufacturerIdNew)) {
                manufacturerIdOld.getProductCollection().remove(product);
                manufacturerIdOld = em.merge(manufacturerIdOld);
            }
            if (manufacturerIdNew != null && !manufacturerIdNew.equals(manufacturerIdOld)) {
                manufacturerIdNew.getProductCollection().add(product);
                manufacturerIdNew = em.merge(manufacturerIdNew);
            }
            if (productCodeOld != null && !productCodeOld.equals(productCodeNew)) {
                productCodeOld.getProductCollection().remove(product);
                productCodeOld = em.merge(productCodeOld);
            }
            if (productCodeNew != null && !productCodeNew.equals(productCodeOld)) {
                productCodeNew.getProductCollection().add(product);
                productCodeNew = em.merge(productCodeNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = product.getProductId();
                if (findProduct(id) == null) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product product;
            try {
                product = em.getReference(Product.class, id);
                product.getProductId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The product with id " + id + " no longer exists.", enfe);
            }
            Manufacturer manufacturerId = product.getManufacturerId();
            if (manufacturerId != null) {
                manufacturerId.getProductCollection().remove(product);
                manufacturerId = em.merge(manufacturerId);
            }
            ProductCode productCode = product.getProductCode();
            if (productCode != null) {
                productCode.getProductCollection().remove(product);
                productCode = em.merge(productCode);
            }
            em.remove(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Product> findProductEntities() {
        return findProductEntities(true, -1, -1);
    }

    public List<Product> findProductEntities(int maxResults, int firstResult) {
        return findProductEntities(false, maxResults, firstResult);
    }

    private List<Product> findProductEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Product.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Product findProduct(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Product> rt = cq.from(Product.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
