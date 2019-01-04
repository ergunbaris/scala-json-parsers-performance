package jsonperf

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}

class UnitTest extends FreeSpec with Matchers with TableDrivenPropertyChecks {

  val tests = Table(
    "test",
    new BigJsonTest
  )

  forAll(tests) { test ⇒
    s"with test ${test.getClass.getName}" - {
      import test._
      val jsonParsings = Table(
        "parser",
        argonautJson, jacksonParsing, json4sJackson, json4sNative, playJson, sphereJson, sprayJson, circeJson, jsoniter, uJson, serdeJson)

      forAll(jsonParsings) { jsonParsing ⇒
        s"using parser '$jsonParsing'" - {
          "should deserialize json" in {
            val json = test.json
            val result = jsonParsing.deserialize(json)
            result shouldEqual test.newA
          }

          "should serialize json" in {
            val a = test.newA
            val result = jsonParsing.serialize(a)
            val aa =
              try jsonParsing.deserialize(result)
              catch {
                case t: Throwable ⇒
                  println(result)
                  throw t
              }
            aa should be (a)
          }
        }
      }
    }
  }

}
