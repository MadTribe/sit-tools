package com.paul.gtd.tools;

public class TJPExporter {

  def projectDefinition(dataModel){
    """
   project "${finalGoal.fullName}" 2011-01-01 +5y {
     # The now date is only set to keep the reports constant. In a real
     # list you would _not_ set a now date.
     now 2011-12-20
   }
"""

  }


  def createProject(dataModel){
      def projectFile = projectDefinition(dataModel);


  }

  def generateTaskList(dataModel){
    
  }

  def template = """

  task "Errands" {
    priority 5

    task "By some milk" {
      end 2011-12-13
      complete 100
      priority 7
    }
    task "Pickup Jacket from dry cleaner" {
      end 2011-12-18
      complete 0
      note "Smith Dry Cleaners"
    }
    task "Buy present for wife" {
      end 2011-12-23
      note "Have a good idea first"
      journalentry 2011-12-10 "Maybe a ring?"
      journalentry 2011-12-14 "Too expensive. Some book?"
    }
  }
  task "Long term projects" {
    priority 3

    task "Buy new car" {
      end 2011-05-11
      complete 100
      priority 6
    }
    task "Build boat" {
      end 2013-04-01
      complete 42
    }
  }

  macro cellcol [
    cellcolor (plan.end < ${now}) & (plan.gauge = "behind schedule") "#FF0000"
    cellcolor plan.gauge = "behind schedule" "#FFFF00"
  ]

  navigator navbar

  textreport frame "" {
    formats html
    header -8<-
      == My ToDo List for ${today} ==
      <[navigator id="navbar"]>
    ->8-
    footer "----"

    columns name,
            end { title "Due Date" ${cellcol} },
            complete,
            priority,
            note,
            journal { celltext 1 ""
                      tooltip 1 "<-query attribute='journal'->"
                      width 70 }

    taskreport "TODOs due today" {
      hidetask (plan.complete >= 100) | (plan.end > %{${now} +1d})
      journalattributes date, headline, summary, details
    }
    taskreport "TODOs due within a week" {
      hidetask (plan.complete >= 100) | (plan.end > %{${now} +1w})
      journalattributes date, headline, summary, details
    }
    taskreport "All open TODOs" {
      hidetask plan.complete >= 100
      journalattributes date, headline, summary, details
    }

    taskreport "Completed TODOs" {
      hidetask plan.complete < 100
    }

    purge formats
  }
"""

}
