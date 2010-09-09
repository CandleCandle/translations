package com.patchmanager.experiments.translations;

import com.patchmanager.experiments.translations.foo.Foo;

/**
 * Hello world!
 *
 */
public class App {

  public static void main(String[] args) {
    System.out.println("trn:" + Foo.get().bar());

//    System.out.println("Title trn : " + Names.get().title());
//    System.out.println("Password trn : " + Names.get().password());
//    System.out.println("Username trn : " + Names.get().username());
//    System.out.println("One param trn : " + Names.get().param("some"));
//    System.out.println("Two params trn : " + Names.get().param("some", "params"));
  }
}
