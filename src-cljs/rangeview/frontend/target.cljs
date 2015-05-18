(ns rangeview.frontend.target)

; The ISSF Rifle and Pistol events, their ISSF abbreviations and the keywords
; used for them in this software. Running target events are excluded since they
; don't use simple ring-based targets.
;
; Some of the pistol events use multiple targets, so have multiple symbols.
;
; Event                        ISSF       Keyword(s)
; -----------------------------------------------------------------
; 10m Air Pistol Men           AP60       :ap60
; 10m Air Pistol Women         AP40       :ap40
; 10m Air Rifle Men            AR60       :ar60
; 10m Air Rifle Women          AR40       :ar40
; 25m Center Fire Pistol Men   CFP        :cfp-precision :cfp-rapid
; 25m Pistol Women             SP         :sp-precision :sp-rapid
; 25m Rapid Fire Pistol Men    RFP        :rfp
; 25m Standard Pistol Men      STP        :stp
; 50m Pistol Men               FP         :fp
; 50m Rifle 3 Positions Men    FR3X40     :fr3x40
; 50m Rifle 3 Positions Women  STR3X20    :str3x20
; 50m Rifle Prone Men          FR60PR     :fr60pr
; 50m Rifle Prone Women        STR60PRW   :str60prw
; 300m Rifle 3 Positions Men   300FR3X40  :300fr3x40
; 300m Rifle 3 Positions Women 300R3X20W  :300r3x20w
; 300m Rifle Prone Men         300FR60PR  :300fr60pr
; 300m Rifle Prone Women       300R60PRW  :300r60prw
; 300m Standard Rifle Men      300STR3X20 :300str3x20

(def target-dimensions
  {:10m-air-pistol [[5 :white :black]
                    [11.5 :white :black]
                    [27.5 :white :black]
                    [43.5 :white :black]
                    [59.5 :black :black]
                    [75.5 :black :white]
                    [91.5 :black :white]
                    [107.5 :black :white]
                    [123.5 :black :white]
                    [139.5 :black :white]
                    [155.5 :black :white]]
   :10m-air-rifle [[0.5 :white :white]
                   [5.5 :white :black]
                   [10.5 :white :black]
                   [15.5 :white :black]
                   [20.5 :white :black]
                   [25.5 :white :black]
                   [30.5 :black :black]
                   [35.5 :black :white]
                   [40.5 :black :white]
                   [45.5 :black :white]]
   :25m-precision-and-50m-pistol [[25 :white :black]
                                  [50 :white :black]
                                  [100 :white :black]
                                  [150 :white :black]
                                  [200 :black :black]
                                  [250 :black :white]
                                  [300 :black :white]
                                  [350 :black :white]
                                  [400 :black :white]
                                  [450 :black :white]
                                  [500 :black :white]]
   :25m-rapid-fire-pistol [[50 :white :black]
                           [100 :white :black]
                           [180 :white :black]
                           [260 :white :black]
                           [340 :white :black]
                           [420 :white :black]
                           [500 :black :black]]
   :50m-rifle [[5 :white :black]
               [10.4 :white :black]
               [26.4 :white :black]
               [42.4 :white :black]
               [58.4 :white :black]
               [74.4 :white :black]
               [90.4 :white :black]
               [106.4 :white :black]
               [112.4 :black :black]
               [122.4 :black :white]
               [138.4 :black :white]
               [154.4 :black :white]]
   :300m-rifle [[50 :white :black]
                [100 :white :black]
                [200 :white :black]
                [300 :white :black]
                [400 :white :black]
                [500 :white :black]
                [600 :black :black]
                [700 :black :white]
                [800 :black :white]
                [900 :black :white]
                [1000 :black :white]]})

(def disciplines
  {:ap60 {:calibre 4.5 :target :10m-air-pistol}
   :ap40 {:calibre 4.5 :target :10m-air-pistol}
   :ar60 {:calibre 4.5 :target :10m-air-rifle}
   :ar40 {:calibre 4.5 :target :10m-air-rifle}
   :cfp-precision {:calibre 9.65 :target :25m-precision-and-50m-pistol}
   :cfp-rapid {:calibre 9.65 :target :25m-rapid-fire-pistol}
   :sp-precision {:calibre 5.6 :target :25m-precision-and-50m-pistol}
   :sp-rapid {:calibre 5.6 :target :25m-rapid-fire-pistol}
   :rfp {:calibre 5.6 :target :25m-rapid-fire-pistol}
   :stp {:calibre 5.6 :target :25m-precision-and-50m-pistol}
   :fp {:calibre 5.6 :target :25m-pistol-precision}
   :fr3x40 {:calibre 5.6 :target :50m-rifle}
   :str3x20 {:calibre 5.6 :target :50m-rifle}
   :fr60pr {:calibre 5.6 :target :50m-rifle}
   :str60prw {:calibre 5.6 :target :50m-rifle}
   :300fr3x40 {:calibre 8.0 :target :300m-rifle}
   :300r3x20w {:calibre 8.0 :target :300m-rifle}
   :300fr60pr {:calibre 8.0 :target :300m-rifle}
   :300r60prw {:calibre 8.0 :target :300m-rifle}
   :300str3x20 {:calibre 8.0 :target :300m-rifle}})

(defn calibre
  "Get the calibre of the round being used for a given target"
  [discipline]
  (-> disciplines discipline :calibre))

(defn rings
  "Get the dimensions and colours for the rings for a target"
  [discipline]
  ((-> disciplines discipline :target) target-dimensions))
