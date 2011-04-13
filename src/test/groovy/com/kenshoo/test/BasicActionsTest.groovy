package com.kenshoo.test

import com.kenshoo.liquibase.LiquidStrap
import groovy.sql.Sql
import org.junit.Before
import org.junit.Test

 /**
 * Created by IntelliJ IDEA.
 * User: ronen
 * Date: 4/5/11
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
class BasicActionsTest {

    def project
    def ds
    def tag = UUID.randomUUID().toString()

    @Before
    public void setup() {
        project = new ProjectStrap().createProjectWithPlugin(com.kenshoo.liquibase.LiquibasePlugin)
        def liqui = project.convention.plugins.liqui
        liqui.configurationScript = 'src/test/resources/liquid.conf'
        def strap = new LiquidStrap()
        strap.readProperties(liqui.configurationScript).with {
            this.ds = strap.createDs(user, pass, host, name)
        }
    }

    public void cleanup() {
        def sql = new Sql(ds)
        ['drop database if exists ronen', 'create database ronen'].each {
            sql.execute(it)
        }
    }

    @Test
    public void reporting() {
        project.verbose = true
        project.contexts = ""
        project.reportStatus.execute()
    }

    @Test
    public void tag() {
      project.tagString = tag
      project.tag.execute()
    }

    @Test
    public void update() {
        project.contexts = ""
        project.update.execute()
        def sql = new Sql(ds)
        sql.execute('select * from play')// will fail if play does not exists
    }
}
