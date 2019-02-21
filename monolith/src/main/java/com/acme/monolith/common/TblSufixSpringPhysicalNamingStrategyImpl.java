package com.acme.monolith.common;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

// referred from the application.yml as a strategy that maps implicitly resolved names to physical names
// we will add `_tbl` sufix to each table so that entity names like `user` does conflict with reserved words in some databases
// NOTE: This class must be public (which is why we did not keep this inside `Launcher`) else hibernate will blow
@SuppressWarnings("unused")
public class TblSufixSpringPhysicalNamingStrategyImpl extends SpringPhysicalNamingStrategy {
  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    Identifier identifier = super.toPhysicalTableName(name, jdbcEnvironment);
    return Identifier.toIdentifier(identifier.getText() + "_tbl", identifier.isQuoted());
  }
}
