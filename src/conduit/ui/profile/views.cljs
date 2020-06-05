(ns conduit.ui.profile.views
  (:require [re-frame.core :as re-frame]
            [steroid.rn.core :as rn]
            [conduit.ui.components :as ui]
            [steroid.rn.components.touchable :as touchable]))

(defn profile-item [title handler first? red?]
  [touchable/touchable-opacity {:on-press handler}
   [rn/view {:style {:padding-horizontal  20 :padding-vertical 15
                     :border-top-width    1 :border-color "rgba(100,100,100,0.2)"
                     :border-bottom-width (if first? 0 1)}}
    [rn/text {:style (merge {:font-size 16}
                            (when red?
                              {:color "#b85c5c"}))}
     title]]])

(defn edit-profile []
  [touchable/touchable-opacity {:on-press #(re-frame/dispatch [:navigate-to :settings])
                                :style    {:padding-horizontal 20 :padding-top 10}}
   [rn/text {:style (merge {:font-size 16})}
    "Edit"]])

(defn profile []
  (let [{:keys [image username bio] :or {username ""}} @(re-frame/subscribe [:user])]
    [ui/safe-area-consumer
     [rn/scroll-view {:style {:flex 1}}
      [rn/view {:style {:align-items :flex-end}}
       [edit-profile]]
      [rn/view {:style {:align-items :center :margin 20 :padding-bottom 40}}
       [ui/userpic image 80]
       [rn/text {:style {:margin-vertical 10 :font-size 20}} username]
       [rn/text {:style           {:color :gray}
                 :number-of-lines 4} bio]]
      [rn/view
       [profile-item "My articles" #(re-frame/dispatch [:set-active-page {:page    :my
                                                                          :profile username}])
        true false]
       [profile-item "Favorited Articles" #(re-frame/dispatch [:set-active-page {:page      :favorited
                                                                                 :favorited username}])
        false false]
       [rn/view {:style {:height 50}}]
       [profile-item "Logout" #(re-frame/dispatch [:logout]) false true]]]]))

(defn profile-old
  []
  (let [{:keys [image username bio following] :or {username ""}} @(re-frame/subscribe [:profile])
        {:keys [author favorites]} @(re-frame/subscribe [:filter])
        loading  @(re-frame/subscribe [:loading])
        articles @(re-frame/subscribe [:articles])
        user     @(re-frame/subscribe [:user])]
    [:div.profile-page
     [:div.user-info
      [:div.container
       [:div.row
        [:div.col-xs-12.col-md-10.offset-md-1
         [:img.user-img {:src image :alt "user image"}]
         [:h4 username]
         [:p bio]
         (if (= (:username user) username)
           [:a.btn.btn-sm.btn-outline-secondary.action-btn  ;{:href (url-for :settings)}
            [:i.ion-gear-a] " Edit Profile Settings"]
           [:button.btn.btn-sm.action-btn.btn-outline-secondary
            {:on-click #(re-frame/dispatch [:toggle-follow-user username])
             :class    (when (:toggle-follow-user loading) "disabled")}
            [:i {:class (if following "ion-minus-round" "ion-plus-round")}]
            [:span (if following (str " Unfollow " username) (str " Follow " username))]])]]]]
     [:div.container
      [:div.row
       [:div.col-xs-12.col-md-10.offset-md-1
        [:div.articles-toggle
         [:ul.nav.nav-pills.outline-active
          [:li.nav-item
           [:a.nav-link                                     ;{:href (url-for :profile :user-id username)
            ;:class (when author " active")
            "My Articles"]]
          [:li.nav-item
           [:a.nav-link {;:href (url-for :favorited :user-id username)
                         :class (when favorites "active")}
            "Favorited Articles"]]]]]]]]))
;[articles-list articles (:articles loading)]]]]]))
