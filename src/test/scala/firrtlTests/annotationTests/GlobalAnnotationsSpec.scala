// See LICENSE for license details.

package firrtlTests
package annotationTests

import firrtlTests._
import firrtl._

class FindTargetDirAnnotation extends Transform {
  def inputForm = HighForm
  def outputForm = HighForm
  var foundTargetDir = false
  def execute(state: CircuitState): CircuitState = {
    println(state.annotations)
    println(s"Running $name")
    state
  }
}

class GlobalAnnotationsSpec extends FirrtlFlatSpec {
  "The target directory" should "be available as an annotation" in {
    val input =
      """circuit Top :
        |  module Top :
        |    input foo : UInt<32>
        |    output bar : UInt<32>
        |    bar <= foo
        """.stripMargin
    val findTargetDir = new FindTargetDirAnnotation
    val targetDir = "a/b/c"

    val optionsManager = new ExecutionOptionsManager("TargetDir") with HasFirrtlOptions {
      commonOptions = commonOptions.copy(targetDirName = targetDir,
                                         topName = "Top")
      firrtlOptions = firrtlOptions.copy(compilerName = "high",
                                         firrtlSource = Some(input),
                                         customTransforms = Seq(findTargetDir))
    }
    Driver.execute(optionsManager)

    // Delete created directory
    val dir = new java.io.File(targetDir)
    dir.exists should be (true)
    FileUtils.deleteDirectoryHierarchy("a") should be (true)
  }
}
