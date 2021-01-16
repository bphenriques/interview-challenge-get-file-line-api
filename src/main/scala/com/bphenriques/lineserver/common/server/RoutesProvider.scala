/*
 *
 *  * © Copyright 2019 Bruno Henriques
 *
 */

package com.bphenriques.lineserver.common.server

import akka.http.scaladsl.server.Route

/** Provides a routes
  */
trait RoutesProvider {

  /** The routes.
    *
    * @return The routes.
    */
  def routes(): Route
}
