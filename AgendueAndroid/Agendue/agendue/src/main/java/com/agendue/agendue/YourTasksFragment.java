package com.agendue.agendue;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.agendue.adapters.YourTasksAdapter;
import com.agendue.model.Project;
import com.agendue.model.Share;
import com.agendue.model.Task;
import com.agendue.web.AgendueDateHandler;
import com.agendue.web.AgendueJSONParser;
import com.agendue.web.AgendueWebHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the callbacks
 * interface.
 */
public class YourTasksFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;

    private List<Task> tasks;
    private List<Project> projects;
    private boolean isIncompleteList;
    private YourTasksAdapter adapter;

    public static YourTasksFragment newInstance() {
        YourTasksFragment fragment = new YourTasksFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public YourTasksFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks_list, container, false);
        isIncompleteList = true;
        if (getListAdapter() == null || getListAdapter().getCount() < 1) {
            GetTasksTask getTasksTask = new GetTasksTask();
            getTasksTask.execute();
        }
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.yourtasks, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.completed_tasks_button) {
            isIncompleteList = !isIncompleteList;
            if(isIncompleteList == true) {
                item.setTitle(getString(R.string.show_complete_tasks));
            } else {
                item.setTitle(getString(R.string.show_incomplete_tasks));
            }
            GetTasksTask getTasksTask = new GetTasksTask();
            getTasksTask.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.replace(R.id.container, TaskDetails.newInstance(tasks.get(position), "3"), "TaskDetails").addToBackStack("TaskDetails").commit();
        //TODO: Make this actually get the right project.
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    private class GetTasksTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                AgendueWebHandler wh = AgendueWebHandler.getInstance();
                if (isIncompleteList == true) {
                    return wh.getYourIncompleteTasks();
                } else {
                    return wh.getYourCompleteTasks();
                }
            } catch (Exception e) {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null && results.equals("error")) {
                Toast.makeText(getActivity(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            } else try {
                tasks = AgendueJSONParser.getTasks(results);
                if (tasks.size() == 2) {
                    if (tasks.get(0).getId() == "-1" && tasks.get(0).getName() == getString(R.string.no_tasks_swipe_from_left)) {
                        tasks.remove(0);
                    }
                }
                adapter = new YourTasksAdapter(tasks, getActivity().getBaseContext());
                setListAdapter(adapter);
            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

}
